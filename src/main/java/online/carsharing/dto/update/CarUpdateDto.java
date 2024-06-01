package online.carsharing.dto.update;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CarUpdateDto {
    @Min(value = 0)
    private int inventory;
}
