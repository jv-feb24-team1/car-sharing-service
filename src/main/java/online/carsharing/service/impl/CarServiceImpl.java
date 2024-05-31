package online.carsharing.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.CarRequestDto;
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
    public CarResponseDto save(CarRequestDto newCar) {
        Car car = carMapper.toCar(newCar);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public CarResponseDto updateById(Long carId, CarUpdateDto carUpdateDto) {
        checkExistence(carId);
        Car car = carRepository.getReferenceById(carId);
        car.setInventory(carUpdateDto.getInventory());
        return carMapper.toDto(carRepository.save(car));
    }

    @Transactional
    @Override
    public CarResponseDto getById(Long carId) {
        checkExistence(carId);
        return carMapper.toDto(carRepository.getReferenceById(carId));
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
