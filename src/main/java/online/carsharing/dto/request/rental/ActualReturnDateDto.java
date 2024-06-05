package online.carsharing.dto.request.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ActualReturnDateDto {
    @NotNull(message = "Actual return date is mandatory")
    private LocalDate actualReturnDate;
}
