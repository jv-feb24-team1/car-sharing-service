package online.carsharing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;
import online.carsharing.entity.Car;

@Data
public class CarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @PositiveOrZero
    @NotNull
    private int inventory;
    @NotNull
    private Car.Type type;
    @NotNull
    @PositiveOrZero
    private BigDecimal dailyFee;
}
