package pet.project.hlib2filestorage.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import pet.project.hlib2filestorage.service.FolderService;

@Configuration
@RequiredArgsConstructor
public class MinioClientConfig {

    private final MinioClientProperties minioClientProperties;

    @Value("minio.bucket.name")
    private String bucketName;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioClientProperties.getEndpoint())
                .credentials(minioClientProperties.getUsername(), minioClientProperties.getPassword())
                .build();
    }


}
