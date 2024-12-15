package pet.project.lgafilestorage.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.dto.folder.*;
import pet.project.lgafilestorage.model.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FolderServiceTest {

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    @Autowired
    private FolderService folderService;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private UserService userService;

    private MockMultipartFile file;

    @BeforeAll
    void init() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }
        userService.save(new UserRegistrationDto("user", "user@gmail.com", "password", null));
        file = new MockMultipartFile("textDocument", "file.txt", MediaType.TEXT_PLAIN_VALUE, "Hello world".getBytes());
    }

    @Test
    void uploadFolder_folderContainsFiles_folderUploadedToUserRootFolder() {
        FolderUploadDto folderUploadDto =
                new FolderUploadDto("folderName", getRootFolder() + "path/folderName/", "user", List.of(file));


        folderService.uploadFolder(folderUploadDto);


        FolderRequestDto folderRequestDto = new FolderRequestDto("folderName", getRootFolder() + "path/folderName/", "user");
        FolderContentDto folderContent = folderService.getFolderContent(folderRequestDto);
        assertThat(folderContent.getFolderPath()).isEqualTo(folderUploadDto.getFolderPath());
        assertThat(folderContent.getObjects().size()).isEqualTo(folderUploadDto.getFolder().size());
    }

    @Test
    void deleteFolder_folderContainsFiles_allFilesIncludingFolderDeleted() {
        FolderUploadDto folderUploadDto =
                new FolderUploadDto("folderName", getRootFolder() + "path/folderName/", "user", List.of(file));
        FolderRequestDto folderRequestDto =
                new FolderRequestDto("folderName", getRootFolder() + "path/folderName/", "user");


        folderService.uploadFolder(folderUploadDto);
        folderService.deleteFolder(folderRequestDto);


        FolderRequestDto rootFolderContentRequest = new FolderRequestDto("", getRootFolder(), "user");
        FolderContentDto rootFolderContent = folderService.getFolderContent(rootFolderContentRequest);
        assertThat(rootFolderContent.getObjects().size()).isZero();
    }

    @Test
    void renameFolder_renameFolderWithFiles_folderRenamedOldFolderIsNotAccessible() {
        FolderUploadDto folderUploadDto =
                new FolderUploadDto("folderName", getRootFolder() + "path/folderName/", "user", List.of(file));
        FolderRenameRequestDto renameRequestDto
                = new FolderRenameRequestDto("NewNameFolder", folderUploadDto.getFolderName(), folderUploadDto.getFolderPath(), folderUploadDto.getUsername());


        folderService.uploadFolder(folderUploadDto);
        folderService.renameFolder(renameRequestDto);


        FolderRequestDto locationOfNewNameFolder = new FolderRequestDto("path", getRootFolder() + "path/", "user");
        FolderContentDto rootFolderContent = folderService.getFolderContent(locationOfNewNameFolder);
        assertThat(
                rootFolderContent.getObjects().stream()
                .filter(e -> e.getObjectName().equals(renameRequestDto.getFolderNewName()))
                .findFirst().get().getObjectName()
        )
                .isEqualTo(renameRequestDto.getFolderNewName());
    }


    @AfterAll
    void cleanUp() {
        FolderRequestDto folderRequestDto = new FolderRequestDto("", rootFolderName, "user");
        folderService.deleteFolder(folderRequestDto);
        if (bucketService.isExist(bucketName)) {
            bucketService.deleteBucket(bucketName);
        }
    }

    private String getRootFolder() {
        Long userId = userService.findByUsername("user").getId();
        return rootFolderName.formatted(userId);
    }
}
