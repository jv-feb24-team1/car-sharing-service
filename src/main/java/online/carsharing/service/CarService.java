package online.carsharing.service;

import java.util.List;
import online.carsharing.dto.request.CarRequestDto;
import online.carsharing.dto.response.CarResponseDto;
import online.carsharing.dto.update.CarUpdateDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto save(CarRequestDto newCar);

    List<CarResponseDto> findAll(Pageable pageable);

    CarResponseDto updateById(Long carId, CarUpdateDto carUpdateDto);

    CarResponseDto getById(Long carId);

    void deleteById(Long carId);
}
