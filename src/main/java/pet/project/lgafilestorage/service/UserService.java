package pet.project.lgafilestorage.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

import static pet.project.lgafilestorage.model.enums.Role.ROLE_USER;
import static pet.project.lgafilestorage.util.UserConverter.toUserJpa;
import static pet.project.lgafilestorage.util.UserConverter.toUserRedis;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AvatarPictureService avatarPictureService;
    private final UserJpaRepository userRepository;
    private final UserRedisRepository redisRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public void save(UserRegistrationDto dto) {
        try {
            User user = User.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .password(encoder.encode(dto.getPassword()))
                    .build();

            AvatarPicture avatarPicture = avatarPictureService.save(dto);
            if (avatarPicture != null) {
                user.setAvatarPicture(avatarPicture);
            }

            User userJpa = userRepository.save(user);

            Role role = roleRepository.save(Role.builder()
                    .role(ROLE_USER.name())
                    .user(userJpa)
                    .build());

            userJpa.setRoles(List.of(role));

            redisRepository.save(toUserRedis(userJpa));
        } catch (Exception e) {
            throw new DatabaseException("Unable to save user: " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        Optional<UserRedis> userRedis = redisRepository.findByUsername(username);

        if (userRedis.isEmpty()) {
            User userJpa = userRepository.findByUsername(username);

            if (userJpa == null) {
                throw new UsernameNotFoundException("User does not exist");
            }

            redisRepository.save(toUserRedis(userJpa));
            return userJpa;
        }
        return toUserJpa(userRedis.get());
    }
}
