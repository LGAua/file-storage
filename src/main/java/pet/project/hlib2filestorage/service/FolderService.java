package pet.project.hlib2filestorage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.hlib2filestorage.exception.FolderOperationException;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.*;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.io.*;
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
                            .object(createAbsolutePath(dto))
                            .stream(new ByteArrayInputStream("Folder placeholder".getBytes()), "Folder placeholder".length(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new FolderOperationException("Can not create folder");
        }
    }

    public FolderContentDto getFolderContent(FolderRequestDto dto) {
        List<FileResponseDto> files = fileService.getFilesInsideFolder(dto);
        List<FolderResponseDto> folders = getFoldersInsideFolder(dto);

        String folderPath = createAbsolutePath(dto);

        return new FolderContentDto(
                folderPath,
                createBreadCrumbs(folderPath),
                folders,
                files
        );
    }

    public void deleteFolder(FolderRequestDto folderRequestDto) {
        deleteObjectsInFolder(folderRequestDto);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(folderRequestDto.getFolderPath())
                            .build()
            );
        } catch (Exception e) {
            throw new FolderOperationException("Can not delete folder");
        }
    }

    public void deleteObjectsInFolder(FolderRequestDto folderRequestDto) {
        List<DeleteObject> deleteObjectList = new ArrayList<>();
        FolderContentDto folderContent = getFolderContent(folderRequestDto);

        //todo common parent
        for (FolderResponseDto folder : folderContent.getFolders()) {
            deleteObjectList.add(new DeleteObject(folder.getFolderPath()));
        }

        for (FileResponseDto file : folderContent.getFiles()) {
            deleteObjectList.add(new DeleteObject(file.getFilePath()));
        }

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(defaultBucketName)
                        .objects(deleteObjectList)
                        .build());

        results.forEach(result -> {
            try {
                result.get();
            } catch (Exception e) {
                throw new FolderOperationException("Can not delete objects in folder");
            }
        });
    }

    public void copyFolderToFolder(FolderResponseDto dto, String folderPath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(folderPath + dto.getFolderName())
                            .source(
                                    CopySource.builder()
                                            .bucket(defaultBucketName)
                                            .object(dto.getFolderPath())
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            throw new FolderOperationException("Can not rename folder");
        }
    }

    public void renameFolder(FolderRenameRequestDto dto) {
        FolderContentDto folderContent = getFolderContent(dto);
        List<FolderResponseDto> folders = folderContent.getFolders();
        List<FileResponseDto> files = folderContent.getFiles();

        String path = createNewPath(dto);

        FolderRequestDto folderRequestDto = new FolderRequestDto(dto.getFolderNewName(), dto.getFolderPath(), dto.getUsername());
        createFolder(folderRequestDto);

        // todo Create common inheritor MinioObjectDto
        for (FolderResponseDto folder : folders) {
            copyFolderToFolder(folder, path);
        }

        for (FileResponseDto file : files) {
            fileService.copyFileToFolder(file, path);
        }

        deleteFolder(dto);
    }

    private String createNewPath(FolderRenameRequestDto dto) {
        String pathToFolder = dto.getFolderPath().substring(0, (dto.getFolderPath().length() - dto.getFolderName().length()) - 1);
        return pathToFolder + dto.getFolderNewName() + "/";
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
                    folderList.add(new FolderResponseDto(objectPath, folderName));
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
        String path = dto.getFolderPath() == null ? rootFolderName : dto.getFolderPath();
        return path.formatted(id);
    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }
}
