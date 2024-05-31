package online.carsharing.dto.response;

import java.math.BigDecimal;
import lombok.Data;
import online.carsharing.entity.Type;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private Type type;
    private BigDecimal dailyFee;
}
