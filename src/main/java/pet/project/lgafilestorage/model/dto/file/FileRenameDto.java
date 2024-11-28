package pet.project.lgafilestorage.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRenameDto extends FileRequestDto {

    private String fileNewName;
}
