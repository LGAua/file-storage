package pet.project.lgafilestorage.model.dto.folder;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderRequestDto {

    private String folderName;

    private String folderPath;

    @NotBlank
    private String username;
}
