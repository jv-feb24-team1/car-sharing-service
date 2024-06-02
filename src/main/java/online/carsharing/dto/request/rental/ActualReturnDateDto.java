package online.carsharing.dto.request.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ActualReturnDateDto {
    private LocalDate actualReturnDate;
}
