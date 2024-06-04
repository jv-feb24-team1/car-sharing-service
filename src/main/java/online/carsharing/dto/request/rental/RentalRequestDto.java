package online.carsharing.dto.request.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import online.carsharing.annotation.ValidRentalDates;

@Data
@ValidRentalDates
public class RentalRequestDto {
    @NotNull(message = "Rental date is mandatory")
    private LocalDate rentalDate;

    @NotNull(message = "Return date is mandatory")
    private LocalDate returnDate;

    @NotNull(message = "Car id is mandatory")
    private Long carId;
}
