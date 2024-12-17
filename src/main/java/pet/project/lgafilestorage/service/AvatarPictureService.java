package pet.project.lgafilestorage.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.project.lgafilestorage.config.BucketConfig;
import pet.project.lgafilestorage.exception.FileOperationException;
import pet.project.lgafilestorage.model.dto.auth.UserRegistrationDto;
import pet.project.lgafilestorage.model.entity.AvatarPicture;
import pet.project.lgafilestorage.repository.AvatarPictureRepository;

@Service
@RequiredArgsConstructor
public class AvatarPictureService {

    @Value("${minio.avatar.folder}")
    private String avatarStorageFolder;

    private final AvatarPictureRepository avatarPictureRepository;
    private final BucketConfig bucketConfig;
    private final MinioClient minioClient;

    public AvatarPicture save(UserRegistrationDto dto) {
        MultipartFile avatar = dto.getFile();
        if (avatar == null) {
            return null;
        }

        String avatarFileName = dto.getUsername() + "-" + avatar.getOriginalFilename();
        try {
            saveAvatarToObjectStorage(dto.getFile(), avatarFileName);
            String objectUrl = getAvatarPictureUrl(avatarFileName);

            return avatarPictureRepository.save(
                    AvatarPicture.builder()
                            .url(objectUrl)
                            .name(avatarFileName)
                            .contentType(avatar.getContentType())
                            .contentSize(avatar.getSize())
                            .build()
            );
        } catch (Exception e) {
            throw new FileOperationException("Unable to save avatar picture: " + e.getMessage());
        }
    }

    private void saveAvatarToObjectStorage(MultipartFile avatar, String avatarFileName) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketConfig.getBucketName())
                        .object(avatarStorageFolder + avatarFileName)
                        .stream(avatar.getInputStream(), avatar.getSize(), -1)
                        .contentType(avatar.getContentType())
                        .build()
        );
    }

    private String getAvatarPictureUrl(String avatarFileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketConfig.getBucketName())
                        .object(avatarStorageFolder + avatarFileName)
                        .method(Method.GET)
                        .build()
        );
    }
}
