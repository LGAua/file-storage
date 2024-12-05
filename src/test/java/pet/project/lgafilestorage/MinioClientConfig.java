package pet.project.lgafilestorage;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pet.project.lgafilestorage.config.MinioClientProperties;

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
