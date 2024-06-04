package online.carsharing.service;

import static online.carsharing.util.CarTestConstants.CAR_BRAND;
import static online.carsharing.util.CarTestConstants.CAR_MODEL;
import static online.carsharing.util.CarTestConstants.DEFAULT_DAILY_FEE;
import static online.carsharing.util.CarTestConstants.DEFAULT_ID;
import static online.carsharing.util.CarTestConstants.DEFAULT_INVENTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import online.carsharing.dto.request.car.CreateCarRequestDto;
import online.carsharing.dto.response.car.CarResponseDto;
import online.carsharing.entity.Car;
import online.carsharing.entity.Type;
import online.carsharing.mapper.CarMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private NotificationService notificationService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    private CarService carService;

    @BeforeEach
    void setUp() {
        carService = new CarServiceImpl(notificationService, carMapper, carRepository);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateCarRequestDto_ReturnsCarResponseDto() {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel(CAR_MODEL);
        requestDto.setBrand(CAR_BRAND);
        requestDto.setType(Type.SEDAN);
        requestDto.setInventory(DEFAULT_INVENTORY);
        requestDto.setDailyFee(DEFAULT_DAILY_FEE);

        Car car = new Car();
        car.setModel(CAR_MODEL);
        car.setBrand(CAR_BRAND);
        car.setType(Type.SEDAN);
        car.setInventory(DEFAULT_INVENTORY);
        car.setDailyFee(DEFAULT_DAILY_FEE);

        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setModel(CAR_MODEL);
        carResponseDto.setBrand(CAR_BRAND);
        carResponseDto.setType(Type.SEDAN);
        carResponseDto.setDailyFee(DEFAULT_DAILY_FEE);

        when(carMapper.toCar(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.save(requestDto);

        assertNotNull(result);
        assertEquals(result.getModel(), requestDto.getModel());
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ReturnsCarResponseDtoList() {
        when(carRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Car())));

        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setModel(CAR_MODEL);
        carResponseDto.setBrand(CAR_BRAND);
        carResponseDto.setType(Type.SEDAN);
        carResponseDto.setDailyFee(DEFAULT_DAILY_FEE);

        when(carMapper.toDto(any())).thenReturn(carResponseDto);

        List<CarResponseDto> result = carService.findAll(Pageable.unpaged());
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getModel(), carResponseDto.getModel());
    }

    @Test
    @DisplayName("Verify getById() method works")
    void getById_ValidId_ReturnsCarResponseDto() {
        Car car = new Car();
        car.setId(DEFAULT_ID);
        car.setModel(CAR_MODEL);
        car.setBrand(CAR_BRAND);
        car.setType(Type.SEDAN);
        car.setDailyFee(DEFAULT_DAILY_FEE);

        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setId(DEFAULT_ID);
        carResponseDto.setModel(CAR_MODEL);
        carResponseDto.setBrand(CAR_BRAND);
        carResponseDto.setType(Type.SEDAN);
        carResponseDto.setDailyFee(DEFAULT_DAILY_FEE);

        when(carRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.getById(DEFAULT_ID);
        assertNotNull(result);
        assertEquals(result.getModel(), carResponseDto.getModel());
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    void deleteById_ValidId_Success() {
        when(carRepository.existsById(DEFAULT_ID)).thenReturn(true);
        doNothing().when(carRepository).deleteById(DEFAULT_ID);
        carService.deleteById(DEFAULT_ID);
        verify(carRepository, times(1)).deleteById(DEFAULT_ID);
    }
}
