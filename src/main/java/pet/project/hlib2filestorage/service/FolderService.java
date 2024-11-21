package pet.project.hlib2filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.hlib2filestorage.exception.FolderOperationException;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderContentDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderRequestDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderResponseDto;
import pet.project.hlib2filestorage.model.dto.folder.FolderUploadDto;
import pet.project.hlib2filestorage.repository.UserRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
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

    public FolderContentDto getFolderContent(FolderRequestDto folderRequestDto) {
        List<FileResponseDto> files = fileService.getFilesInsideFolder(folderRequestDto);
        List<FolderResponseDto> folders = getFoldersInsideFolder(folderRequestDto);

        String folderPath = createAbsolutePath(folderRequestDto);

        return new FolderContentDto(
                folderPath,
                createBreadCrumbs(folderPath),
                folders,
                files
        );
    }

    public List<FolderResponseDto> getFoldersInsideFolder(FolderRequestDto dto) {
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
                    String folderName = objectPath.substring(objectPath.indexOf("/") + 1, objectPath.lastIndexOf("/"));
                    folderList.add(new FolderResponseDto(objectPath, folderName));
                }
            } catch (Exception e) {
                log.error("Can not retrieve folder list");
                throw new FolderOperationException("Can not retrieve folder list");
            }
        }
        return folderList;
    }

    private Long getIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

    private String createAbsolutePath(FolderRequestDto dto) {
        Long id = getIdByUsername(dto.getUsername());
        String path = dto.getFolderPath() == null ? rootFolderName : dto.getFolderPath();

        return path.formatted(id);
    }

    // user-1-file/lifeboat/img_01
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
}
