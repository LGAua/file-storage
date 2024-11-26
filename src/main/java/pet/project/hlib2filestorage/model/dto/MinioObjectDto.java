package pet.project.hlib2filestorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinioObjectDto {
    private String objectName;
    private String objectPath;

    @Override
    public int hashCode() {
        int PRIME = 31;
        return this.getObjectName().hashCode() * this.getObjectPath().hashCode() * PRIME;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.hashCode() == this.hashCode() || this == obj) return true;

        MinioObjectDto minioObj = (MinioObjectDto) obj;

        return minioObj.getObjectName().equals(this.getObjectName()) &&
                minioObj.getObjectPath().equals(this.getObjectName());
    }
}
