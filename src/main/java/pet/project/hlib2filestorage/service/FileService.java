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
import pet.project.hlib2filestorage.model.dto.MinioObjectDto;
import pet.project.hlib2filestorage.model.dto.file.FileDownloadDto;
import pet.project.hlib2filestorage.model.dto.file.FileRequestDto;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.file.FileUploadDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String path = createAbsolutePath(fileUploadDto.getUsername(), null); //remove null
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(path + file.getOriginalFilename())
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
        } catch (Exception e) {
            log.error("Can not save file in folder: %s".formatted(fileUploadDto.getFolderName()));
            throw new FileOperationException("Can not save file in folder: %s".formatted(fileUploadDto.getFolderName()));
        }
    }

    public FileDownloadDto getFile(FileRequestDto dto) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(defaultBucketName)
                .object(dto.getFilePath())
                .build();

        try (GetObjectResponse object = minioClient.getObject(getObjectArgs)) {
            String contentType = object.headers().get("Content-type");
            String contentLength = object.headers().get("Content-length");
            assert contentLength != null;

            return new FileDownloadDto(
                    object.readAllBytes(),
                    contentType,
                    Long.parseLong(contentLength)
            );
        } catch (Exception e) {
            throw new FileOperationException("Can not get the file. " + e.getMessage());
        }
    }

    public List<FileResponseDto> getFilesInsideFolder(FolderRequestDto dto) {
        List<FileResponseDto> fileList = new ArrayList<>();
        String path = createAbsolutePath(dto.getUsername(), dto.getFolderPath());

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
                    if (!fileName.isBlank()) {
                        fileList.add(new FileResponseDto(objectPath, fileName));
                    }
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return fileList;

    }

    public void copyFileToFolder(FileResponseDto dto, String folderPath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(folderPath + dto.getFileName())
                            .source(
                                    CopySource.builder()
                                            .bucket(defaultBucketName)
                                            .object(dto.getFilePath())
                                            .build()
                            )
                            .build()
            );
        } catch (Exception e) {
            throw new FolderOperationException("Can not rename folder");
        }

    }

    public void deleteFile(FileRequestDto fileRequestDto) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(fileRequestDto.getFilePath())
                            .build());
        } catch (Exception e) {
            throw new FileOperationException("Can not delete file");
        }
    }

    private String createAbsolutePath(String username, String folderPath) {
        Long id = getIdByUsername(username);
        String path = folderPath == null ? rootFolderName : folderPath;
        return path.formatted(id);
    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

    public Set<MinioObjectDto> findObjectsByName(FileRequestDto dto) {
        Set<MinioObjectDto> objectList = new HashSet<>();
        String objectName = dto.getObjectName();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(defaultBucketName)
                        .prefix(createAbsolutePath(dto.getUsername(), null))
                        .recursive(true)
                        .build());

        results.forEach(result -> {
            try {
                String objectPath = result.get().objectName();
                if (objectPath.contains(objectName)) {
//                    int endIndex = objectPath.indexOf(objectName) + objectName.length();
                    String path = objectPath.substring(0, objectPath.indexOf(objectName));
                    objectList.add(new MinioObjectDto(objectName, path));
                }
            } catch (Exception e) {
                throw new FileOperationException("Error during searching object with name " + dto.getObjectName());
            }
        });

        return objectList;
    }
}