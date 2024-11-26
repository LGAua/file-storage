package pet.project.hlib2filestorage.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadDto {

    private byte[] file;

    private String contentType;

    private Long contentLength;
}
