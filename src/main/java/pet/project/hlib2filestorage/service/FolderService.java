package pet.project.hlib2filestorage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.hlib2filestorage.exception.FileOperationException;
import pet.project.hlib2filestorage.exception.FolderOperationException;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.*;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderService {

    private final MinioClient minioClient;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    @Value("${minio.bucket.name}")
    private String defaultBucketName;

    public void uploadFolder(FolderUploadDto dto) {
        Long id = getIdByUsername(dto.getUsername());
        try {
            for (MultipartFile file : dto.getFolder()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(defaultBucketName)
                                .contentType(file.getContentType())
                                .object(rootFolderName.formatted(id) + file.getOriginalFilename().replaceAll(" ", "_"))
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .build());
            }
        } catch (Exception e) {
            log.error("Can not upload folder");
            throw new FolderOperationException("Can not upload folder");
        }

    }

    public void createFolder(FolderRequestDto dto) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(createEmptyFolderPath(dto))
                            .stream(new ByteArrayInputStream("Folder placeholder".getBytes()), "Folder placeholder".length(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new FolderOperationException("Can not create folder");
        }

    }

    public FolderContentDto getFolderContent(FolderRequestDto folderRequestDto) {
        List<FileResponseDto> files = fileService.getFilesInsideFolder(folderRequestDto);
        List<FolderResponseDto> folders = getFoldersInsideFolder(folderRequestDto);

        String folderPath = createAbsolutePath(folderRequestDto); //не правильно, просто dto.getFolderPath()

        return new FolderContentDto(
                folderPath,
                createBreadCrumbs(folderPath),
                folders,
                files
        );
    }

    private List<FolderResponseDto> getFoldersInsideFolder(FolderRequestDto dto) {
        List<FolderResponseDto> folderList = new ArrayList<>();
        String path = createAbsolutePath(dto);

        Iterable<Result<Item>> objectsInsideFolder = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(defaultBucketName)
                        .prefix(path)
                        .recursive(false)
                        .build());

        for (Result<Item> object : objectsInsideFolder) {
            try {
                String objectPath = object.get().objectName();
                if (object.get().isDir()) {
                    String folderName = objectPath.substring(path.length(), objectPath.lastIndexOf("/"));
                    folderList.add(new FolderResponseDto(path, folderName));
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return folderList;
    }

    private Map<String, String> createBreadCrumbs(String folderPath) {
        Map<String, String> breadCrumbsAndFolderPath = new TreeMap<>(Comparator.comparingInt(folderPath::indexOf));

        String[] breadCrumbs = folderPath.split("/");
        int endIndex = 0;

        for (String breadCrumb : breadCrumbs) {
            endIndex += (breadCrumb.length() + 1);
            String path = folderPath.substring(0, endIndex);

            breadCrumbsAndFolderPath.put(breadCrumb, path);
        }
        return breadCrumbsAndFolderPath;
    }

    private String createAbsolutePath(FolderRequestDto dto) {
        Long id = getIdByUsername(dto.getUsername());
        String path = dto.getFolderPath() == null ? rootFolderName : (dto.getFolderPath() + dto.getFolderName() + "/");
        return path.formatted(id);
    }

    private String createEmptyFolderPath(FolderRequestDto dto) {
        return createAbsolutePath(dto) + dto.getFolderName() + "/";
    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

    public void deleteFolder(FolderRequestDto fileRequestDto) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(fileRequestDto.getFolderPath())
                            .build());
        } catch (Exception e) {
            throw new FileOperationException("Can not delete folder");
        }
    }

    public void copyFolderToFolder(FolderResponseDto dto, String folderPath) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(defaultBucketName)
                .object(dto.getFolderPath() + dto.getFolderName())
                .build();
        try (GetObjectResponse object = minioClient.getObject(getObjectArgs)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(defaultBucketName)
                    .object(folderPath + dto.getFolderName())
                    .stream(object, object.readAllBytes().length, 1)
                    .build());

        } catch (Exception e) {
            throw new FolderOperationException("Can not rename folder");
        }
    }

    public void renameFolder(FolderRenameRequestDto dto) {
        FolderContentDto folderContent = getFolderContent(dto);
        List<FolderResponseDto> folders = folderContent.getFolders();
        List<FileResponseDto> files = folderContent.getFiles();

        String path = dto.getFolderPath() + dto.getFolderNewName();

        // Не создает
        createFolder(dto);
        // Maybe change to SnowBallObjects (need to check with timer)
        for (FileResponseDto file : files) {
            fileService.copyFileToFolder(file, path);
        }

        for (FolderResponseDto folder : folders) {
            copyFolderToFolder(folder, path);
        }

        //не удаляет
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(createAbsolutePath(dto))
                            .build());
        } catch (Exception e) {
            throw new FolderOperationException("Can not rename folder");
        }
    }
}
