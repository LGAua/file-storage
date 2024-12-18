package pet.project.lgafilestorage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinioObjectDto {
    protected String objectName;
    protected String objectPath;
    private boolean isDir;

    public MinioObjectDto(String objectName, String objectPath) {
        this.objectName = objectName;
        this.objectPath = objectPath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, objectPath, isDir);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Сравнение на идентичность ссылок
        if (obj == null || getClass() != obj.getClass()) return false; // Проверка на null и тип объекта

        MinioObjectDto other = (MinioObjectDto) obj;

        return Objects.equals(objectName, other.objectName) &&
                Objects.equals(objectPath, other.objectPath) &&
                isDir == other.isDir; // Сравнение всех полей
    }
}
