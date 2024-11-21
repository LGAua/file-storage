package pet.project.hlib2filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.hlib2filestorage.exception.FileOperationException;
import pet.project.hlib2filestorage.exception.FolderOperationException;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderResponseDto;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final UserRepository userRepository;

    @Value("${minio.bucket.name}")
    private String defaultBucketName;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    public void saveObject(FileUploadDto fileUploadDto) {
        MultipartFile file = fileUploadDto.getFile();
        Long id = userRepository.findByUsername(fileUploadDto.getUsername()).getId();
        String pathToObject = rootFolderName.formatted(id) + file.getOriginalFilename();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(fileUploadDto.getFolderName())
                            .object(pathToObject)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
        } catch (Exception e) {
            log.error("Can not save file in folder: %s".formatted(fileUploadDto.getFolderName()));
            throw new FileOperationException("Can not save file in folder: %s".formatted(fileUploadDto.getFolderName()));
        }
    }

    // Is it the same as public void saveObject ?? need to test
    public void downloadObject(String fileName) {
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .filename(fileName)
                            .build()
            );
        } catch (Exception e) {

        }
    }

    public byte[] getFile(FileRequestDto dto) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(defaultBucketName)
                .object(dto.getFilePath())
                .build();

        try (GetObjectResponse object = minioClient.getObject(getObjectArgs)) {
            return object.readAllBytes();
        } catch (Exception e) {
            throw new FileOperationException("Can not get the file");
        }
    }

    public List<FileResponseDto> getFilesInsideFolder(FolderRequestDto dto) {
        List<FileResponseDto> fileList = new ArrayList<>();
        Long id = getIdByUsername(dto.getUsername());
        String path = createAbsolutePath(id, dto.getFolderPath());

        Iterable<Result<Item>> objectsInsideFolder = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(defaultBucketName)
                        .prefix(path)
                        .recursive(false)
                        .build());

        for (Result<Item> object : objectsInsideFolder) {
            try {
                String objectPath = object.get().objectName();
                if (!object.get().isDir()) {
                    String fileName = objectPath.substring(objectPath.lastIndexOf("/") + 1);

                    fileList.add(new FileResponseDto(
                            objectPath,
                            fileName
                    ));
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return fileList;

    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

    private String createAbsolutePath(Long userId, String folderPath) {
        String path = folderPath == null ? rootFolderName : folderPath;
        return path.formatted(userId);
    }

    public List<String> createBreadCrumbs(String folderPath, String folderName) {
        String path = folderPath.substring(0, (folderPath.length() - folderName.length()));
        return List.of(path.split("/"));
    }
}