package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.rental.ActualReturnDateDto;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.exception.CarIsNotAvailableException;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.exception.InvalidInputDataException;
import online.carsharing.exception.UnauthorizedAccessException;
import online.carsharing.mapper.RentalMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.RentalService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int INVENTORY_MIN_VALUE = 1;
    private static final int INVENTORY_ADJUSTMENT = 1;
    private static final String ROLE_MANAGER = "ROLE_MANAGER";

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalResponseDto save(RentalRequestDto rentalDto) {
        Car car = getCar(rentalDto.getCarId());
        if (car.getInventory() < INVENTORY_MIN_VALUE) {
            throw new CarIsNotAvailableException("Currently, this car is unavailable");
        }
        car.setInventory(car.getInventory() - INVENTORY_ADJUSTMENT);
        carRepository.save(car);

        User user = getUser(rentalDto.getUserId());
        Rental rental = rentalMapper.toEntity(rentalDto);
        rental.setUser(user);
        rental.setCar(car);

        Rental savedRental = rentalRepository.save(rental);
        RentalResponseDto responseDto = rentalMapper.toDto(savedRental);
        responseDto.setId(savedRental.getId());
        return responseDto;
    }

    @Override
    public RentalResponseDto getById(Long rentalId) {
        Rental rental = getRental(rentalId);
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalResponseDto> getUserRentals(User user, Long userId, boolean isActive) {
        if (userId != null && isNotManager(user) && !userId.equals(user.getId())) {
            throw new UnauthorizedAccessException(
                    "User does not have access to rentals of another user");
        }

        List<Rental> rentals = (userId != null)
                ? rentalRepository.findAllByUserIdAndActive(userId, isActive)
                : rentalRepository.findAllByUserIdAndActive(user.getId(), isActive);

        return rentals.stream()
                      .map(rentalMapper::toDto)
                      .toList();
    }

    @Override
    public RentalResponseDto setActualReturnDate(Long rentalId, ActualReturnDateDto requestDto) {
        Rental rental = getRental(rentalId);

        if (!rental.isActive()) {
            throw new InvalidInputDataException("This rental in not active");
        }

        rental.setActualReturnDate(requestDto.getActualReturnDate());
        rental.setActive(false);

        Car car = rental.getCar();
        car.setInventory(car.getInventory() + INVENTORY_ADJUSTMENT);
        carRepository.save(car);

        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with id " + userId));
    }

    private Car getCar(Long carId) {
        return carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Car not found with id " + carId));
    }

    private Rental getRental(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Rental not found with id " + rentalId));
    }

    private boolean isNotManager(User user) {
        return user.getAuthorities().stream()
                   .map(GrantedAuthority::getAuthority)
                   .noneMatch(authority -> authority.equals(ROLE_MANAGER));
    }
}
