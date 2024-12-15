package pet.project.lgafilestorage.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.dto.file.FileRequestDto;
import pet.project.lgafilestorage.model.dto.file.FileUploadDto;
import pet.project.lgafilestorage.model.dto.folder.FolderRequestDto;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;
import pet.project.lgafilestorage.service.*;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class SearchControllerTest {

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    @MockBean
    private UserRedisRepository redisRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private FileService fileService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private SearchService searchService;

    private MockMultipartFile file;

    @BeforeAll
    void init() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }

        userService.save(new UserRegistrationDto("user", "user@gmail.com", "password", null));
        file = new MockMultipartFile("note", "note.txt", MediaType.TEXT_PLAIN_VALUE, "Hello world".getBytes());
    }

    /**
     * Acknowledgement: Folder should be created with {@link FolderService#createFolder} method to be found by search service
     */
    @Test
    void searchRequest_userSearchForExistingFile_receiveAllFilesAndFoldersWithTheSameCharsSequence() {
        FolderRequestDto folder = folderService.createFolder(new FolderRequestDto("Notes", rootFolderName + "Notes/", "user"));
        String filename = file.getOriginalFilename();
        FileUploadDto fileDto = new FileUploadDto(file, filename, folder.getFolderPath(), "user");
        fileService.uploadFile(fileDto);


        FileRequestDto fileRequestDto = new FileRequestDto();
        fileRequestDto.setObjectName("Note");
        fileRequestDto.setUsername(fileDto.getUsername());
        Set<MinioObjectDto> objects = searchService.findObjectsByName(fileRequestDto);


        assertThat(objects.size()).isEqualTo(2);
        assertThat(
                objects.stream()
                        .map(MinioObjectDto::getObjectName)
                        .collect(toList())
        ).containsExactlyInAnyOrderElementsOf(List.of("Notes", "note.txt"));
    }

    @Test
    void searchRequest_bucketContainsFilesOfDifferentUsers_userReceivesOnlyHisFiles() {
        userService.save(new UserRegistrationDto("secondTestUser", "user2@gmail.com", "password", null));
        MockMultipartFile secondUserFile =
                new MockMultipartFile("sunset", "sunset.png", MediaType.IMAGE_PNG_VALUE, "Beautiful sunset".getBytes());
        FileUploadDto secondUserUploadDto =
                new FileUploadDto(secondUserFile, secondUserFile.getOriginalFilename(), rootFolderName, "secondTestUser");
        fileService.uploadFile(secondUserUploadDto);


        FileRequestDto fileRequestDto = new FileRequestDto();
        fileRequestDto.setObjectName("sunset");
        fileRequestDto.setUsername("user");
        Set<MinioObjectDto> objects = searchService.findObjectsByName(fileRequestDto);


        assertThat(objects.size()).isEqualTo(0);
        deleteUserFiles("secondTestUser");
    }

    @AfterAll
    void cleanUp() {
        FolderRequestDto folderRequestDto = new FolderRequestDto("", rootFolderName, "user");
        folderService.deleteFolder(folderRequestDto);
        if (bucketService.isExist(bucketName)) {
            bucketService.deleteBucket(bucketName);
        }
    }

    private void deleteUserFiles(String username) {
        FolderRequestDto folderRequestDto = new FolderRequestDto("", rootFolderName, username);
        folderService.deleteFolder(folderRequestDto);
    }
}
