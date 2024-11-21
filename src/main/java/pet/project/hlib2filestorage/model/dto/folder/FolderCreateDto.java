package pet.project.hlib2filestorage.model.dto.folder;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderCreateDto {

    @NotBlank
    private String folderName;
}
