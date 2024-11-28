package pet.project.lgafilestorage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio.client")
@Component
public class MinioClientProperties {
    private String endpoint;
    private String username;
    private String password;
}
