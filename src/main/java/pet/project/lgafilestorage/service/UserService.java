package pet.project.lgafilestorage.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pet.project.lgafilestorage.config.BucketConfig;
import pet.project.lgafilestorage.exception.DatabaseException;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.entity.AvatarPicture;
import pet.project.lgafilestorage.model.entity.Role;
import pet.project.lgafilestorage.model.entity.User;
import pet.project.lgafilestorage.model.redis.UserRedis;
import pet.project.lgafilestorage.repository.AvatarPictureRepository;
import pet.project.lgafilestorage.repository.RoleRepository;
import pet.project.lgafilestorage.repository.redis.UserRedisRepository;
import pet.project.lgafilestorage.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static pet.project.lgafilestorage.model.enums.Role.ROLE_USER;
import static pet.project.lgafilestorage.util.UserConverter.toUserJpa;
import static pet.project.lgafilestorage.util.UserConverter.toUserRedis;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String FOLDER_FOR_AVATAR_STORAGE = "users-avatars/";

    private final UserJpaRepository userRepository;
    private final UserRedisRepository redisRepository;
    private final AvatarPictureRepository avatarPictureRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;
    private final MinioClient minioClient;
    private final BucketConfig bucketConfig;

//    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public void save(UserRegistrationDto dto) {
        try {
            AvatarPicture avatarPicture = avatarPictureRepository.save(saveAvatarImage(dto));

            User user = userRepository.save(
                    User.builder()
                            .username(dto.getUsername())
                            .email(dto.getEmail())
                            .password(encoder.encode(dto.getPassword()))
                            .avatarPicture(avatarPicture)
                            .build());

            Role role = roleRepository.save(Role.builder()
                    .role(ROLE_USER.name())
                    .user(user)
                    .build());

            user.setRoles(List.of(role));

            redisRepository.save(toUserRedis(user));
        } catch (Exception e) {
            throw new DatabaseException("Unable to save user: " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        Optional<UserRedis> userRedis = redisRepository.findByUsername(username);

        if (userRedis.isEmpty()) {
            User userJpa = userRepository.findByUsername(username);

            if (userJpa == null){
                throw new UsernameNotFoundException("User does not exist");
            }

            redisRepository.save(toUserRedis(userJpa));
            return userJpa;
        }
        return toUserJpa(userRedis.get());
    }

    private AvatarPicture saveAvatarImage(UserRegistrationDto dto) {
        MultipartFile avatar = dto.getFile();
        String avatarFileName = dto.getUsername() + "-" + avatar.getOriginalFilename();

        try {
            minioClient.putObject(
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

            return AvatarPicture.builder()
                    .url(objectUrl)
                    .name(avatarFileName)
                    .contentType(avatar.getContentType())
                    .contentSize(avatar.getSize())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Can not save the user avatar: " + e.getMessage());
        }
    }

    //todo private builders for entities
}
