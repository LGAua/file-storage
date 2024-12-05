package pet.project.lgafilestorage;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.ApplicationEventsTestExecutionListener;
import pet.project.lgafilestorage.service.BucketService;

@Component
@TestExecutionListeners(listeners = {ApplicationEventsTestExecutionListener.class})
public class BucketConfig {

    private static final String TEST_BUCKET_NAME = "test-user-bucket";
    @Autowired
    private static BucketService bucketService;

    @Autowired
    private MinioClient minioClient;

    static {
        if (!bucketService.isExist(TEST_BUCKET_NAME)) {
            bucketService.createBucket(TEST_BUCKET_NAME);
        }
    }

    public void createBucket(){}
}
