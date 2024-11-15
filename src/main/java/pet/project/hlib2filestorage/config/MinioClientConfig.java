package pet.project.hlib2filestorage.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioClientConfig {

    private final MinioClientProperties minioClientProperties;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioClientProperties.getEndpoint())
                .credentials(minioClientProperties.getUsername(), minioClientProperties.getPassword())
                .build();
    }
}
