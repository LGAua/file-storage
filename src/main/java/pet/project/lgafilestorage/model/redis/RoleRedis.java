package pet.project.lgafilestorage.model.redis;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pet.project.lgafilestorage.model.entity.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRedis {
    private Long id;

    private String role;
}
