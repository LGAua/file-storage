package pet.project.hlib2filestorage.model.dto.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDto {

    @NotBlank
    private String folderName;

    @NotNull(message = "You must specify the file to upload")
    private MultipartFile file;

    private String username;
}
