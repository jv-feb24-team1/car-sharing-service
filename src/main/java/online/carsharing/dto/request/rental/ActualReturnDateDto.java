package online.carsharing.dto.request.rental;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ActualReturnDateDto {
    @NotBlank(message = "Actual return date is mandatory")
    private LocalDate actualReturnDate;
}
