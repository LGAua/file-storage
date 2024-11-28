package pet.project.lgafilestorage.model.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderRenameRequestDto extends FolderRequestDto{

    private String folderNewName;
}
