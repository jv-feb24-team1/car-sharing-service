package online.carsharing.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import online.carsharing.annotation.PasswordMatches;

@Data
@PasswordMatches
public class UserRegisterRequestDto {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm password is mandatory")
    @Size(min = 8, message = "Confirm password should be at least 8 characters long")
    private String confirmPassword;
}

