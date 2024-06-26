package online.carsharing.dto.response.rental;

import java.time.LocalDate;
import lombok.Data;
import online.carsharing.dto.response.car.CarResponseDto;

@Data
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarResponseDto car;
}
