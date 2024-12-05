package pet.project.lgafilestorage.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.lgafilestorage.config.BucketConfig;
import pet.project.lgafilestorage.exception.DatabaseException;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String FOLDER_FOR_AVATAR_STORAGE = "users-avatars/";

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final MinioClient minioClient;
    private final BucketConfig bucketConfig;

    public void save(UserRegistrationDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .avatarUrl(saveAvatarImage(dto))
                .roles(Set.of("ROLE_USER"))
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException("User with such email or username already exist");
        }
    }

    private String saveAvatarImage(UserRegistrationDto dto) {
        MultipartFile avatar = dto.getFile();
        String avatarFileName = dto.getUsername() + "-" + avatar.getName();

        try {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketConfig.getBucketName())
                            .object(FOLDER_FOR_AVATAR_STORAGE + avatarFileName)
                            .stream(avatar.getInputStream(), avatar.getSize(), -1)
                            .contentType(avatar.getContentType())
                            .build());

            String objectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketConfig.getBucketName())
                            .object(FOLDER_FOR_AVATAR_STORAGE + avatarFileName)
                            .method(Method.GET)
                            .build());
            return objectUrl;
        } catch (Exception e) {
            throw new RuntimeException("Can not save the user avatar: " + e.getMessage());
        }
    }
}
