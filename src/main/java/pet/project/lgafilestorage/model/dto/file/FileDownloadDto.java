package pet.project.lgafilestorage.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadDto {

    private ByteArrayResource file;
    private String contentType;
    private Long contentLength;
}
