package pet.project.hlib2filestorage.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
