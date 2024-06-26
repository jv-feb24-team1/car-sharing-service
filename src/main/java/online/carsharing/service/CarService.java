package online.carsharing.service;

import java.util.List;
import online.carsharing.dto.request.car.CreateCarRequestDto;
import online.carsharing.dto.response.car.CarResponseDto;
import online.carsharing.dto.update.car.CarUpdateDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto save(CreateCarRequestDto newCar);

    List<CarResponseDto> findAll(Pageable pageable);

    CarResponseDto updateById(Long carId, CarUpdateDto carUpdateDto);

    CarResponseDto getById(Long carId);

    void deleteById(Long carId);
}
