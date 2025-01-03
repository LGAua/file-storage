package pet.project.lgafilestorage.model.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.lgafilestorage.model.dto.BreadCrumbDto;
import pet.project.lgafilestorage.model.dto.MinioObjectDto;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderContentDto {

    private String folderPath;
    private List<BreadCrumbDto> breadCrumbs;
    private List<MinioObjectDto> objects;
}
