package online.carsharing.dto.request.rental;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalRequestDto {
    @NotBlank(message = "Rental date is mandatory")
    private LocalDate rentalDate;

    @NotBlank(message = "Return date is mandatory")
    private LocalDate returnDate;

    @NotBlank(message = "Car id is mandatory")
    private Long carId;

    @NotBlank(message = "User id is mandatory")
    private Long userId;
}
