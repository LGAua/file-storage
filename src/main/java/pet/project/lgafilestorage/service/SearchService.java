package pet.project.lgafilestorage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pet.project.lgafilestorage.exception.FileOperationException;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SearchService {

    private final MinioClient minioClient;
    private final UserRepository userRepository;

    @Value("${minio.bucket.name}")
    private String defaultBucketName;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    public Set<MinioObjectDto> findObjectsByName(FileRequestDto dto) {
        Set<MinioObjectDto> searchResult = new HashSet<>();

        Iterable<Result<Item>> userObjects = getUserObjects(dto);
        String searchName = dto.getObjectName();

        userObjects.forEach(objectPathResult -> {
            try {
                String objectPath = objectPathResult.get().objectName();
                String name = getObjectFullName(objectPath);

                if (objectPath.toLowerCase().contains(searchName.toLowerCase())
                        && (name.toLowerCase()).contains(searchName.toLowerCase())) {

                    String path = objectPath.substring(0, objectPath.lastIndexOf(name));
                    searchResult.add(new MinioObjectDto(name, path));
                }
            } catch (Exception e) {
                throw new FileOperationException("Error during searching object with name " + dto.getObjectName());
            }
        });
        return searchResult;
    }

    public Iterable<Result<Item>> getUserObjects(FileRequestDto dto) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(defaultBucketName)
                        .prefix(createRootPath(dto.getUsername()))
                        .recursive(true)
                        .build());
    }

    private String getObjectFullName(String objectPath) {
        String[] pathElements = objectPath.split("/");
        int lastElementIndex = pathElements.length - 1;
        return pathElements[lastElementIndex];
    }

    private String createRootPath(String username) {
        Long id = userRepository.findByUsername(username).getId();
        return rootFolderName.formatted(id);
    }
}