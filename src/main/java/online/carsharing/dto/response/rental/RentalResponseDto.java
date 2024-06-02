package online.carsharing.dto.response.rental;

import java.time.LocalDate;
import lombok.Data;
import online.carsharing.entity.Car;

@Data
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Car car;
}
