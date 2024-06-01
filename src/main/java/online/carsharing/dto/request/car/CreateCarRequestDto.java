package online.carsharing.dto.request.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;
import online.carsharing.entity.Type;

@Data
public class CreateCarRequestDto {
    @NotBlank(message = "Car model can't be null")
    private String model;
    @NotBlank(message = "Car brand can't be null")
    private String brand;
    @Min(value = 0, message = "Inventory can't contain less then 0 elements")
    private int inventory;
    @NotNull(message = "Car type can't be null")
    private Type type;
    @NotNull
    @PositiveOrZero(message = "Daily fee can't be less then 0$")
    private BigDecimal dailyFee;
}
