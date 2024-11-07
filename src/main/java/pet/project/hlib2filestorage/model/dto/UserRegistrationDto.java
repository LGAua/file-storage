package pet.project.hlib2filestorage.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Email can nit be blank")
    private String username;

    @NotBlank(message = "Email can nit be blank")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank
    @Size(min = 5,message = "Password should have at least 5 symbols")
    private String password;
}
