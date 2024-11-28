package pet.project.lgafilestorage.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Email can not be blank")
    private String username;

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank
    @Size(min = 5,message = "Password should have at least 5 symbols")
    private String password;
}
