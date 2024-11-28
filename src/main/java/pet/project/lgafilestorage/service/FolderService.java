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
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.FileResponseDto;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.repository.UserRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
        if (dto.getFolder() == null || dto.getFolder().isEmpty()) {
            createFolder(dto);
            return;
        }

        try {
            for (MultipartFile file : dto.getFolder()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(defaultBucketName)
                                .contentType(file.getContentType())
                                .object(createAbsolutePath(dto))
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .build());
            }
        } catch (Exception e) {
            log.error("Can not upload folder");
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
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

    private Map<String, String> createBreadCrumbs(String folderPath) {
        Map<String, String> breadCrumbsAndFolderPath = new LinkedHashMap<>();

        String[] breadCrumbs = folderPath.split("/");
        int endIndex = 0;

        for (String breadCrumb : breadCrumbs) {
            endIndex += (breadCrumb.length() + 1);
            String path = folderPath.substring(0, endIndex);
// todo Дело в том что:
//  (1) или надо использовать здесь не мапу и создлавать папки с одинаковыми названиями,
//  (2) или создавать новые папки с порядковыми номерами и тогда мапу можно оставит
            if (breadCrumbsAndFolderPath.containsKey(breadCrumb)) {
                String finalBreadCrumb = breadCrumb;

                Map<String, String> map = breadCrumbsAndFolderPath.entrySet().stream()
                        .filter((e) -> e.getKey().contains(finalBreadCrumb))
                        .collect(toMap(
                                (k) -> k.getKey(),
                                (v) -> v.getValue())
                        );
                breadCrumb = breadCrumb + map.size();
            }

            breadCrumbsAndFolderPath.put(breadCrumb, path);
        }
        return breadCrumbsAndFolderPath;
    }

    private String createNewPath(FolderRenameRequestDto dto, String objectPath) {
        String pathToFolder = dto.getFolderPath().substring(0, (dto.getFolderPath().length() - dto.getFolderName().length()) - 1);
        String newPath = pathToFolder + dto.getFolderNewName() + "/";
        return objectPath.replace(dto.getFolderPath(), newPath);
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
