package pet.project.hlib2filestorage.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pet.project.hlib2filestorage.model.entity.User;

@Data
public class UserLoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
