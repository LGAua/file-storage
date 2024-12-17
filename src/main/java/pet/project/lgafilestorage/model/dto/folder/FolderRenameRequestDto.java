package pet.project.lgafilestorage.model.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.lgafilestorage.constraints.annotations.FolderRenameDuplicateNames;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FolderRenameDuplicateNames
public class FolderRenameRequestDto extends FolderRequestDto {

    private String folderNewName;

    public FolderRenameRequestDto(String folderNewName, String folderName, String folderPath, String username) {
        super(folderName, folderPath, username);
        this.folderNewName = folderNewName;
    }
}
