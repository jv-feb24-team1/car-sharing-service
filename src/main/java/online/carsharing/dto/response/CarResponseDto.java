package online.carsharing.dto.response;

import java.math.BigDecimal;
import lombok.Data;
import online.carsharing.entity.Car;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private Car.Type type;
    private BigDecimal dailyFee;
}
