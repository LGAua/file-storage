package pet.project.lgafilestorage.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvatarPictureRedis {

    private Long id;

    private String url;

    private String contentType;

    private Long contentSize;
}
