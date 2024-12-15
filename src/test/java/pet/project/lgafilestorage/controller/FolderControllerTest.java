package pet.project.lgafilestorage.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pet.project.lgafilestorage.model.dto.folder.FolderContentDto;
import pet.project.lgafilestorage.model.dto.folder.FolderRequestDto;
import pet.project.lgafilestorage.model.entity.User;
import org.springframework.test.web.servlet.MockMvc;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.dto.folder.FolderUploadDto;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;
import pet.project.lgafilestorage.service.BucketService;
import pet.project.lgafilestorage.service.FolderService;
import pet.project.lgafilestorage.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FolderControllerTest {

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.folder.name.template}")
    private String rootFolderName;

    @MockBean
    private UserRedisRepository redisRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private BucketService bucketService;

    private MockMultipartFile file;

    @BeforeAll
    void init() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }
        userService.save(new UserRegistrationDto("user", "user@gmail.com", "password", null));
        userService.save(new UserRegistrationDto("secondTestUser", "user2@gmail.com", "password", null));

        file = new MockMultipartFile("textDocument", "file.txt", MediaType.TEXT_PLAIN_VALUE, "Hello world".getBytes());
    }

    @ParameterizedTest
    @WithMockUser(username = "user")
    @ValueSource(strings = {"user", "secondTestUser"})
    void sendControllerRequestForFiles_TryToAccessAnotherUserFiles_HttpResponseForbidden(String username) throws Exception {
        User secondUser = userService.findByUsername("secondTestUser");
        String rootFolderOfSecondUser = rootFolderName.formatted(secondUser.getId());
        FolderUploadDto secondUserUploadDto =
                new FolderUploadDto("folderName", rootFolderOfSecondUser + "path/folderName/", "secondTestUser", List.of(file));


        folderService.uploadFolder(secondUserUploadDto);


        mvc.perform(get("/folder").param("path", rootFolderOfSecondUser))
                .andExpect(status().isForbidden());

        cleanUp(username);
    }


    private void cleanUp(String username) {
        FolderRequestDto folderRequestDto = new FolderRequestDto("", rootFolderName, username);
        folderService.deleteFolder(folderRequestDto);
    }

    @AfterAll
    void removeBucket() {
        if (bucketService.isExist(bucketName)) {
            bucketService.deleteBucket(bucketName);
        }
    }
}