package online.carsharing.dto.request.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalRequestDto {
    @NotNull(message = "Rental date is mandatory")
    private LocalDate rentalDate;

    @NotNull(message = "Return date is mandatory")
    private LocalDate returnDate;

    @NotNull(message = "Car id is mandatory")
    private Long carId;

    @NotNull(message = "User id is mandatory")
    private Long userId;
}
