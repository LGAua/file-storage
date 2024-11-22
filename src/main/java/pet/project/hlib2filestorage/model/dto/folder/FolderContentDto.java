package pet.project.hlib2filestorage.model.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.hlib2filestorage.model.dto.file.FileResponseDto;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderContentDto {

    private String folderPath;
    private Map<String ,String> breadCrumbs;
    private List<FolderResponseDto> folders;
    private List<FileResponseDto> files;
}