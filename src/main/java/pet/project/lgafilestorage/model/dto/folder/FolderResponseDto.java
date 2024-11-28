package pet.project.lgafilestorage.model.dto.folder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;

@Getter
@Setter
@NoArgsConstructor
public class FolderResponseDto extends MinioObjectDto {

    public FolderResponseDto(String folderName, String folderPath, boolean isDir) {
        super(folderName, folderPath, isDir);
    }
}
