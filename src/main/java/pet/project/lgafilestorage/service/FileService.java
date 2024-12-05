package pet.project.lgafilestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.lgafilestorage.exception.FileOperationException;
import pet.project.lgafilestorage.exception.FolderOperationException;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.*;
import pet.project.lgafilestorage.model.dto.folder.FolderRequestDto;
import pet.project.lgafilestorage.repository.UserRepository;

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

    public void uploadFile(FileUploadDto fileUploadDto) {
        MultipartFile file = fileUploadDto.getFile();
        String path = createAbsolutePath(fileUploadDto.getUsername(), fileUploadDto.getFolderPath());
        fileUploadDto.setFolderPath(path);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(path + file.getOriginalFilename())
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
        } catch (Exception e) {
            log.error("Can not save file: %s".formatted(fileUploadDto.getFolderPath()));
            throw new FileOperationException("Can not save file: %s".formatted(fileUploadDto.getFolderPath()));
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
                    new ByteArrayResource(object.readAllBytes()),
                    contentType,
                    Long.parseLong(contentLength)
            );
        } catch (Exception e) {
            throw new FileOperationException("Can not get the file. " + e.getMessage());
        }
    }

    public List<MinioObjectDto> getFilesInsideFolder(FolderRequestDto dto) {
        List<MinioObjectDto> fileList = new ArrayList<>();
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
                        fileList.add(new FileResponseDto(fileName, objectPath));
                    }
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return fileList;

    }

    public void copyFileToFolder(FileResponseDto dto, String filePath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(filePath)
                            .source(
                                    CopySource.builder()
                                            .bucket(defaultBucketName)
                                            .object(dto.getObjectPath())
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

    public void renameFile(FileRenameDto dto) {
        String fileName = dto.getFileName().substring(0, dto.getFileName().indexOf("."));
        String newPath = dto.getFilePath().replace(fileName, dto.getFileNewName());
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(defaultBucketName)
                            .object(newPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(defaultBucketName)
                                            .object(dto.getFilePath())
                                            .build()
                            )
                            .build());
        } catch (Exception e) {
            throw new FileOperationException(e.getMessage());
        }

        deleteFile(dto);
    }

    private String getObjectFullName(String objectPath) {
        String[] strings = objectPath.split("/");
        return strings[strings.length - 1];
    }

    private String createAbsolutePath(String username, String folderPath) {
        Long id = getIdByUsername(username);
        String path = folderPath == null ? rootFolderName : folderPath;
        return path.formatted(id);
    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }
}