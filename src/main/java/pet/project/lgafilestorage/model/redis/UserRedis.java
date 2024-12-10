package pet.project.lgafilestorage.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;
import java.util.Set;

@RedisHash(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRedis {

    @Id
    private String id;

    @Indexed
    private String username;

    private String email;

    private String password;

    private AvatarPictureRedis avatarPicture;

    private List<RoleRedis> roles;

    @TimeToLive
    private Long timeout;

}
