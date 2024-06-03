package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.car.CreateCarRequestDto;
import online.carsharing.dto.response.car.CarResponseDto;
import online.carsharing.dto.update.car.CarUpdateDto;
import online.carsharing.entity.Car;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.mapper.CarMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.service.CarService;
import online.carsharing.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final NotificationService notificationService;
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Override
    public CarResponseDto save(CreateCarRequestDto newCar) {
        Car car = carMapper.toCar(newCar);
        notificationService.createCarNotification(car);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarResponseDto updateById(Long carId, CarUpdateDto carUpdateDto) {
        Car car = findCarById(carId);
        car.setInventory(carUpdateDto.getInventory());
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarResponseDto getById(Long carId) {
        return carMapper.toDto(findCarById(carId));
    }

    @Override
    public void deleteById(Long carId) {
        checkExistence(carId);
        carRepository.deleteById(carId);
    }

    private void checkExistence(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find car by id " + id);
        }
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + carId));
    }
}
