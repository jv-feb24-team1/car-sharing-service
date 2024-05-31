package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.CreateCarRequestDto;
import online.carsharing.dto.response.CarResponseDto;
import online.carsharing.dto.update.CarUpdateDto;
import online.carsharing.entity.Car;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.mapper.CarMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Override
    public CarResponseDto save(CreateCarRequestDto newCar) {
        Car car = carMapper.toCar(newCar);
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
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + carId));
        car.setInventory(carUpdateDto.getInventory());
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarResponseDto getById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + carId));
        return carMapper.toDto(car);
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
}
