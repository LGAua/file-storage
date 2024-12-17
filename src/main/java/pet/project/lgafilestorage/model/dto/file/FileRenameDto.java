package pet.project.lgafilestorage.model.dto.file;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.lgafilestorage.constraints.annotations.FileRenameDuplicateNames;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FileRenameDuplicateNames
public class FileRenameDto extends FileRequestDto {

    @NotBlank
    private String fileNewName;
}
