package online.carsharing.dto.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CarUpdateDto {
    @PositiveOrZero
    @NotNull
    private int inventory;
}
