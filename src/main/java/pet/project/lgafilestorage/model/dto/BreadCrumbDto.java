package pet.project.lgafilestorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BreadCrumbDto {
    private String breadCrumbName;
    private String breadCrumbPath;
}
