package pet.project.lgafilestorage.service;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import pet.project.lgafilestorage.exception.DatabaseException;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.dto.folder.FolderRequestDto;
import pet.project.lgafilestorage.model.entity.Role;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.repository.AvatarPictureRepository;
import pet.project.lgafilestorage.repository.RoleRepository;
import pet.project.lgafilestorage.repository.UserJpaRepository;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    @Value("${minio.bucket.name}")
    private String bucketName;
    @Value("${minio.folder.name.template}")
    private String rootFolderName;
    @Value("${minio.avatar.folder}")
    private String avatarsPictureFolder;

    @MockBean
    private UserRedisRepository userRedisRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private FolderService folderService;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AvatarPictureRepository avatarPictureRepository;

    @Autowired
    private MinioClient minioClient;

    @BeforeAll
    void init() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }
    }

    @BeforeEach
    void cleanUp() {
        roleRepository.deleteAll();
        userJpaRepository.deleteAll();
        avatarPictureRepository.deleteAll();
    }

    @Test
    void saveUser_UniqueUsername_SaveToTablesUserAndRolesAndAvatarPicture() {
        MockMultipartFile file =
                new MockMultipartFile("avatar", "avatar.png", MediaType.IMAGE_PNG_VALUE, "avatarPicture".getBytes());
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("user", "user@gmail.com", "password", file);


        userService.save(userRegistrationDto);


        User user = userJpaRepository.findByUsername("user");
        assertThat(user.getEmail()).isEqualTo(userRegistrationDto.getEmail());
        assertThat(user.getAvatarPicture().getContentType()).isEqualTo(file.getContentType());
        assertThat(
                user.getRoles().stream()
                        .map(Role::getRole)
                        .collect(toList())
        ).isEqualTo(List.of("ROLE_USER"));
    }

    @Test
    void saveUser_NotUniqueUsername_ThrowsDataBaseException() {
        MockMultipartFile file =
                new MockMultipartFile("avatar", "avatar.png", MediaType.IMAGE_PNG_VALUE, "avatarPicture".getBytes());
        UserRegistrationDto userWithUniqueUsername =
                new UserRegistrationDto("user", "user@gmail.com", "password", file);
        UserRegistrationDto userWithNotUniqueUsername =
                new UserRegistrationDto("user", "testuser@gmail.com", "password", file);


        userService.save(userWithUniqueUsername);


        assertThrowsExactly(DatabaseException.class, () -> userService.save(userWithNotUniqueUsername));
    }


    @Test
    void findByUsername_UserExists_ReturnUserJpaEntity() {
        MockMultipartFile file =
                new MockMultipartFile("avatar", "avatar.png", MediaType.IMAGE_PNG_VALUE, "avatarPicture".getBytes());
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("user", "user@gmail.com", "password", file);


        userService.save(userRegistrationDto);
        User user = userService.findByUsername("user");


        assertThat(user.getEmail()).isEqualTo(userRegistrationDto.getEmail());
        assertThat(user.getAvatarPicture().getContentType()).isEqualTo(file.getContentType());
        assertThat(user).isInstanceOf(User.class);
    }

    @Test
    void findByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {

        assertThrowsExactly(UsernameNotFoundException.class, () -> userService.findByUsername("notExistingUsername"));
    }

    @AfterAll
    void removeBucket() {
        FolderRequestDto folderRequestDto = new FolderRequestDto("", rootFolderName, "user");
        folderService.deleteFolder(folderRequestDto);
        deleteAvatarFolderInMinioStorage();
        if (bucketService.isExist(bucketName)) {
            bucketService.deleteBucket(bucketName);
        }
    }

    private void deleteAvatarFolderInMinioStorage() {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(avatarsPictureFolder + "user-avatar.png")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
