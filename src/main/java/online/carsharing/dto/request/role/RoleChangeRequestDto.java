package online.carsharing.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleChangeRequestDto {
    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "CUSTOMER|MANAGER", message = "Role must be either CUSTOMER or MANAGER")
    private String role;
}
