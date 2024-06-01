package online.carsharing.mapper;

import online.carsharing.config.MapperConfig;
import online.carsharing.dto.request.CreateCarRequestDto;
import online.carsharing.dto.response.CarResponseDto;
import online.carsharing.entity.Car;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toDto(Car car);

    Car toCar(CreateCarRequestDto requestDto);
}
