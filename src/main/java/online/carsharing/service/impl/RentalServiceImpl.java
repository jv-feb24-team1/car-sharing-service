package online.carsharing.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.exception.CarIsNotAvailableException;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.mapper.RentalMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.NotificationService;
import online.carsharing.service.RentalService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int INVERTORY_ADJUSTMENT = 1;

    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;

    @Override
    public RentalResponseDto save(RentalRequestDto rentalDto) {
        User user = getUser(rentalDto.getUserId());
        Car car = getCar(rentalDto.getCarId());

        if (car.getInventory() < 1) {
            throw new CarIsNotAvailableException("This car is not available");
        }
        car.setInventory((car.getInventory()) - INVERTORY_ADJUSTMENT);

        Rental rental = rentalMapper.toEntity(rentalDto);
        rental.setUser(user);
        rental.setCar(car);

        Rental savedRental = rentalRepository.save(rental);

        RentalResponseDto responseDto = rentalMapper.toDto(savedRental);
        responseDto.setId(savedRental.getId());

        notificationService.createRental(rental);
        return responseDto;
    }

    @Override
    public RentalResponseDto getById(Long rentalId) {
        return null;
    }

    @Override
    public List<RentalResponseDto> getByUserId(Long userId) {
        return null;
    }

    @Override
    public List<RentalResponseDto> findAll() {
        return null;
    }

    @Override
    public RentalRequestDto setActualReturnDate(Long rentalId, LocalDate actualDate) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new EntityNotFoundException("User not found"));
    }

    private Car getCar(Long carID) {
        return carRepository.findById(carID).orElseThrow(() ->
            new EntityNotFoundException("Car not found"));
    }
}
