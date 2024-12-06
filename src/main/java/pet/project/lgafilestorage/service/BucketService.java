package pet.project.lgafilestorage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pet.project.lgafilestorage.exception.FileOperationException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BucketService {

    private final MinioClient minioClient;

    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName.toLowerCase())
                            .build());
        } catch (Exception e) {
            log.error("Can not create folder");
            throw new FileOperationException("Can not create the bucket: " + bucketName, e.getCause());
        }
    }

    public boolean isExist(String bucketName) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
        } catch (Exception e) {
            log.error("Can not check folder existence");
            throw new FileOperationException("Can not check bucket existence: " + bucketName, e.getCause());
        }
    }
}