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
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.model.redis.UserRedis;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;
import pet.project.lgafilestorage.repository.UserJpaRepository;

import java.util.Optional;
import java.util.Set;

import static pet.project.lgafilestorage.util.UserConverter.toUserJpa;
import static pet.project.lgafilestorage.util.UserConverter.toUserRedis;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String FOLDER_FOR_AVATAR_STORAGE = "users-avatars/";

    private final UserJpaRepository userRepository;
    private final UserRedisRepository redisRepository;
    private final PasswordEncoder encoder;
    private final MinioClient minioClient;
    private final BucketConfig bucketConfig;

    public void save(UserRegistrationDto dto) {
        User user = userRepository.save(
                User.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .password(encoder.encode(dto.getPassword()))
                        .avatarUrl(saveAvatarImage(dto))
                        .roles(Set.of("ROLE_USER"))
                        .build()
        );

        redisRepository.save(toUserRedis(user));
    }

    public User findByUsername(String username) {
        Optional<UserRedis> userRedis = redisRepository.findByUsername(username);

        if (userRedis.isEmpty()) {
            User userJpa = userRepository.findByUsername(username);
            redisRepository.save(toUserRedis(userJpa));

            return userJpa;
        }

        return toUserJpa(userRedis.get());
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

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketConfig.getBucketName())
                            .object(FOLDER_FOR_AVATAR_STORAGE + avatarFileName)
                            .method(Method.GET)
                            .build());

        } catch (Exception e) {
            throw new RuntimeException("Can not save the user avatar: " + e.getMessage());
        }
    }
}
