package pet.project.lgafilestorage.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pet.project.lgafilestorage.service.BucketService;

@Component
@RequiredArgsConstructor
public class BucketConfig {

    private final BucketService bucketService;

    @Value("${minio.bucket.name}")
    @Getter
    private String bucketName;

    @EventListener(ContextRefreshedEvent.class)
    public void bucketCreation() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }
    }
}
