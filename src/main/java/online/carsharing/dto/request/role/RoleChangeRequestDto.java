package online.carsharing.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleChangeRequestDto {
    @NotBlank(message = "Role is mandatory")
    private String role;
}
