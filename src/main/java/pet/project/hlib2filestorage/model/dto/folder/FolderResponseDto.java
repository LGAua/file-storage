package pet.project.hlib2filestorage.model.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.hlib2filestorage.model.dto.MinioObjectDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FolderResponseDto extends MinioObjectDto {

    public FolderResponseDto(String folderName, String folderPath, boolean isDir) {
        super(folderName, folderPath, isDir);
    }
}
