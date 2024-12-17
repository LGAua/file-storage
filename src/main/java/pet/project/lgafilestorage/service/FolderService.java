package pet.project.lgafilestorage.service;

import io.minio.*;
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
import pet.project.lgafilestorage.exception.FolderOperationException;
import pet.project.lgafilestorage.model.dto.BreadCrumbDto;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.FileResponseDto;
import pet.project.lgafilestorage.model.dto.folder.*;

import java.io.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderService {

    private final MinioClient minioClient;
    private final UserService userService;
    private final FileService fileService;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    @Value("${minio.bucket.name}")
    private String defaultBucketName;

    public FolderContentDto getFolderContent(FolderRequestDto dto) {
        List<MinioObjectDto> objects = new ArrayList<>();

        objects.addAll(getFoldersInsideFolder(dto));
        objects.addAll(fileService.getFilesInsideFolder(dto));

        String folderPath = createAbsolutePath(dto);

        return new FolderContentDto(
                folderPath,
                createBreadCrumbs(folderPath),
                objects
        );
    }

    public void uploadFolder(FolderUploadDto dto) {
        try {
            for (MultipartFile file : dto.getFolder()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(defaultBucketName)
                                .contentType(file.getContentType())
                                .object(createAbsolutePath(dto) + file.getOriginalFilename())
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .build());
            }
        } catch (Exception e) {
            log.error("Can not upload folder");
            throw new FolderOperationException("Can not upload folder");
        }
    }

    public FolderRequestDto createFolder(FolderRequestDto dto) {
        String folderLocation = getFolderLocation(dto);
        int folderCounter = getAmountOfDuplicateFolders(dto, folderLocation);

        if (folderCounter != 0) {
            String folderName = "%s (%s)/".formatted(dto.getFolderName(), folderCounter);
            dto.setFolderPath(folderLocation + folderName);
        }

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

        dto.setFolderPath(folderLocation);
        return dto;
    }

    public void deleteFolder(FolderRequestDto folderRequestDto) {
        List<MinioObjectDto> objects = getAllObjectsWithGivenRoot(folderRequestDto);

        List<DeleteObject> deleteObjectList = new ArrayList<>();
        deleteObjectList.add(new DeleteObject(folderRequestDto.getFolderPath()));

        objects.forEach(object ->
                deleteObjectList.add(new DeleteObject(object.getObjectPath()))
        );

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

    public void renameFolder(FolderRenameRequestDto dto) {
        List<MinioObjectDto> allObjectsWithGivenRoot = getAllObjectsWithGivenRoot(dto);

        if (allObjectsWithGivenRoot.isEmpty()) {
            String path = dto.getFolderPath().replace(dto.getFolderName(), dto.getFolderNewName());
            createFolder(new FolderRequestDto(dto.getFolderNewName(), path, dto.getUsername()));
        }

        allObjectsWithGivenRoot.forEach(object -> {
            String path = createNewPath(dto, object.getObjectPath());

            if (object.isDir()) {
                createFolder(new FolderRequestDto(object.getObjectName(), path, dto.getUsername()));
            } else {
                fileService.copyFileToFolder((FileResponseDto) object, path);
            }
        });

        deleteFolder(dto);
    }

    private List<MinioObjectDto> getAllObjectsWithGivenRoot(FolderRequestDto dto) {
        List<MinioObjectDto> allObjectsInsideFolder = new ArrayList<>();
        List<MinioObjectDto> folderContent = getFolderContent(dto).getObjects();

        for (MinioObjectDto objectDto : folderContent) {
            if (objectDto.isDir()) {
                FolderRequestDto folderRequestDto = new FolderRequestDto(objectDto.getObjectName(), objectDto.getObjectPath(), dto.getUsername());
                if (!getFolderContent(folderRequestDto).getObjects().isEmpty()) {
                    allObjectsInsideFolder.addAll(getAllObjectsWithGivenRoot(folderRequestDto));
                }
            }
            allObjectsInsideFolder.add(objectDto);
        }
        return allObjectsInsideFolder;
    }

    private int getAmountOfDuplicateFolders(FolderRequestDto dto, String folderLocation) {
        List<MinioObjectDto> minioObjectList =
                getFoldersInsideFolder(new FolderRequestDto(dto.getFolderName(), folderLocation, dto.getUsername())).stream()
                        .filter(minioObject -> minioObject.getObjectName().contains(dto.getFolderName()))
                        .toList();

        return minioObjectList.size();
    }

    private String getFolderLocation(FolderRequestDto dto) {
        String folderPath = dto.getFolderPath();
        return folderPath.substring(0, folderPath.lastIndexOf(dto.getFolderName() + "/"));
    }

    private List<MinioObjectDto> getFoldersInsideFolder(FolderRequestDto dto) {
        List<MinioObjectDto> folderList = new ArrayList<>();
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
                    folderList.add(new FolderResponseDto(folderName, objectPath, true));
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return folderList;
    }

    private List<BreadCrumbDto> createBreadCrumbs(String folderPath) {
        List<BreadCrumbDto> breadCrumbList = new ArrayList<>();

        String[] breadCrumbs = folderPath.split("/");
        int endIndex = 0;

        for (String breadCrumb : breadCrumbs) {
            endIndex += (breadCrumb.length() + 1);
            String path = folderPath.substring(0, endIndex);

            breadCrumbList.add(new BreadCrumbDto(breadCrumb, path));
        }
        return breadCrumbList;
    }

    private String createNewPath(FolderRenameRequestDto dto, String objectPath) {
        String pathToFolder = dto.getFolderPath().substring(0, (dto.getFolderPath().length() - dto.getFolderName().length()) - 1);
        String newPath = pathToFolder + dto.getFolderNewName() + "/";
        return objectPath.replace(dto.getFolderPath(), newPath);
    }

    private String createAbsolutePath(FolderRequestDto dto) {
        Long id = getIdByUsername(dto.getUsername());

        String path;
        if (dto.getFolderPath() == null || dto.getFolderPath().isEmpty()) {
            path = rootFolderName;
        } else {
            path = dto.getFolderPath();
        }

        return path.formatted(id);
    }

    private Long getIdByUsername(String username) {
        return userService.findByUsername(username).getId();
    }
}
