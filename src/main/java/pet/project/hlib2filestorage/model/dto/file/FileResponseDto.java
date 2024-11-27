package pet.project.hlib2filestorage.model.dto.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.hlib2filestorage.model.dto.MinioObjectDto;

@Getter
@Setter
@NoArgsConstructor
public class FileResponseDto extends MinioObjectDto {

    public FileResponseDto(String fileName, String filePath){
        super(fileName,filePath);
    }
}
