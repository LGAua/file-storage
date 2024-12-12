package pet.project.lgafilestorage.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FolderServiceTest {

    @Autowired
    private BucketService bucketService;

    @Value("${minio.bucket.name}")
    private static String bucketName;

    @BeforeAll
    void init() {
        if (!bucketService.isExist(bucketName)) {
            bucketService.createBucket(bucketName);
        }
    }

    @AfterAll
    void cleanUp() {
        if (bucketService.isExist(bucketName)) {
            bucketService.deleteBucket(bucketName);
        }
    }


}
