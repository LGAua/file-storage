package pet.project.hlib2filestorage.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.hlib2filestorage.model.dto.MinioObjectDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto extends MinioObjectDto {

    private String fileName;
    private String filePath;
    private String username;
}
