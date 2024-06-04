package online.carsharing.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import online.carsharing.dto.request.rental.ActualReturnDateDto;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.car.CarResponseDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.Role;
import online.carsharing.entity.RoleType;
import online.carsharing.entity.User;
import online.carsharing.exception.CarIsNotAvailableException;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.exception.InvalidInputDataException;
import online.carsharing.exception.UnauthorizedAccessException;
import online.carsharing.mapper.RentalMapper;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {
    private static final int ACTIVE_RENTALS_AMOUNT = 3;
    private static final LocalDate RENTAL_DATE = LocalDate.now().minusDays(ACTIVE_RENTALS_AMOUNT);
    private static final int INVENTORY_ADJASTMENT = 1;
    private static final LocalDate RETURN_DATE = LocalDate.now().plusDays(INVENTORY_ADJASTMENT);
    private static final long CAR_ID = 1L;
    private static final long MANAGER_ID = 1L;
    private static final long CUSTOMER_ID = 2L;
    private static final long RENTAL_ID = 1L;
    private static final int INACTIVE_RENTALS_AMOUNT = 5;
    private static final long NON_EXISTENT_RENTAL_ID = 100L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Rental validRental;
    private User manager;
    private User customer;
    private Car car;

    @BeforeEach
    public void setUp() {
        Role customerRole = new Role();
        customerRole.setType(RoleType.CUSTOMER);

        Role managerRole = new Role();
        managerRole.setType(RoleType.MANAGER);

        manager = new User();
        manager.setId(MANAGER_ID);
        manager.setRoles(Set.of(managerRole));

        customer = new User();
        customer.setId(CUSTOMER_ID);
        customer.setRoles(Set.of(customerRole));

        car = new Car();
        car.setId(CAR_ID);
        car.setInventory(2);

        validRental = new Rental();
        validRental.setRentalDate(RENTAL_DATE);
        validRental.setReturnDate(RETURN_DATE);
        validRental.setCar(car);
        validRental.setUser(customer);
        validRental.setActive(true);
    }

    @Test
    @DisplayName("Save rental (positive)")
    public void save_ValidRentalDto_ReturnsResponseDto() {
        RentalRequestDto rentalRequestDto = getRentalCreateDto(validRental);
        RentalResponseDto responseDto = getRentalResponseDto(validRental);

        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(rentalMapper.toEntity(rentalRequestDto)).thenReturn(validRental);
        when(rentalRepository.save(validRental)).thenReturn(validRental);
        when(rentalMapper.toDto(validRental)).thenReturn(responseDto);

        doNothing().when(notificationService).createRentalNotification(validRental);

        RentalResponseDto savedRentalResponseDto = rentalService.save(customer, rentalRequestDto);

        assertNotNull(savedRentalResponseDto);
        assertEquals(responseDto, savedRentalResponseDto);

        verify(userRepository).findById(CUSTOMER_ID);
        verify(carRepository).findById(CAR_ID);
        verify(carRepository).save(car);
        verify(rentalRepository).save(validRental);
        verify(notificationService).createRentalNotification(validRental);
    }

    @Test
    @DisplayName("Save rental throws CarIsNotAvailableException for car with zero inventory")
    public void save_CarWithZeroInventory_ThrowsCarIsNotAvailableException() {
        RentalRequestDto rentalRequestDto = getRentalCreateDto(validRental);
        car.setInventory(0);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));

        assertThrows(CarIsNotAvailableException.class, () ->
                rentalService.save(customer, rentalRequestDto));
    }

    @Test
    @DisplayName("Save rental throws EntityNotFoundException for non-existent car")
    public void save_NonExistentCar_ThrowsEntityNotFoundException() {
        RentalRequestDto rentalRequestDto = getRentalCreateDto(validRental);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rentalService.save(customer, rentalRequestDto));
    }

    @Test
    @DisplayName("Save rental throws EntityNotFoundException for invalid user ID")
    public void save_InvalidUserId_ThrowsEntityNotFoundException() {
        RentalRequestDto rentalDto = getRentalCreateDto(validRental);

        when(carRepository.findById(rentalDto.getCarId())).thenReturn(Optional.of(car));

        assertThrows(EntityNotFoundException.class, () -> rentalService.save(customer, rentalDto));
    }

    @Test
    @DisplayName("getById retrieves valid rental")
    public void getById_ValidRental_RentalResponseDto() {
        RentalResponseDto expectedResponseDto = getRentalResponseDto(validRental);

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(validRental));
        when(rentalMapper.toDto(validRental)).thenReturn(expectedResponseDto);

        RentalResponseDto actualRentalResponseDto = rentalService.getById(RENTAL_ID);

        assertEquals(expectedResponseDto, actualRentalResponseDto);
        verify(rentalMapper).toDto(validRental);
    }

    @Test
    @DisplayName("getById throws EntityNotFoundException for non-existent rental")
    public void getById_NonExistentRental_ThrowsException() {
        when(rentalRepository.findById(NON_EXISTENT_RENTAL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                rentalService.getById(NON_EXISTENT_RENTAL_ID));
    }

    @Test
    @DisplayName("getUserRentals throws UnauthorizedAccessException for non-manager "
            + "accessing another user's rentals")
    public void getUserRentals_UnauthorizedAccess_ThrowsException() {
        Long anotherUserId = MANAGER_ID;
        assertThrows(UnauthorizedAccessException.class,
                () -> rentalService.getUserRentals(customer, anotherUserId, true));
    }

    @Test
    @DisplayName("getUserRentals retrieves active and inactive rentals of all users by manager")
    public void getUserRentals_ManagerRetrievesAllActiveAndInactive_ListOfRentalResponseDto() {
        List<Rental> activeRentals = generateRentals(true, ACTIVE_RENTALS_AMOUNT);
        List<Rental> inactiveRentals = generateRentals(false, INACTIVE_RENTALS_AMOUNT);

        when(rentalRepository.findAllByActive(true)).thenReturn(activeRentals);
        when(rentalRepository.findAllByActive(false)).thenReturn(inactiveRentals);

        List<RentalResponseDto> allActiveRentals =
                rentalService.getUserRentals(manager, null, true);

        assertNotNull(allActiveRentals);
        assertFalse(allActiveRentals.isEmpty());
        assertEquals(ACTIVE_RENTALS_AMOUNT, allActiveRentals.size());

        List<RentalResponseDto> allInactiveRentals =
                rentalService.getUserRentals(manager, null, false);

        assertNotNull(allInactiveRentals);
        assertFalse(allInactiveRentals.isEmpty());
        assertEquals(INACTIVE_RENTALS_AMOUNT, allInactiveRentals.size());

        verify(rentalRepository).findAllByActive(true);
        verify(rentalRepository).findAllByActive(false);
    }

    @Test
    @DisplayName("getUserRentals retrieves inactive users rentals by manager")
    public void getUserRentals_ManagerRetrievesUsersInactiveRentals_ListOfRentalResponseDto() {
        List<Rental> inactiveRentals = generateRentals(false, INACTIVE_RENTALS_AMOUNT);
        Long userId = customer.getId();

        when(rentalRepository.findAllByUserIdAndActive(userId, false)).thenReturn(inactiveRentals);

        List<RentalResponseDto> usersInactiveRentals =
                rentalService.getUserRentals(manager, userId, false);

        assertNotNull(usersInactiveRentals);
        assertFalse(usersInactiveRentals.isEmpty());
        assertEquals(INACTIVE_RENTALS_AMOUNT, usersInactiveRentals.size());

        verify(rentalRepository).findAllByUserIdAndActive(userId, false);
    }

    @Test
    @DisplayName("getUserRentals retrieves active rentals for customer")
    public void getUserRentals_CustomerRetrievesActive() {
        List<Rental> activeRentals = generateRentals(false, INACTIVE_RENTALS_AMOUNT);
        Long userId = customer.getId();

        when(rentalRepository.findAllByUserIdAndActive(userId, true)).thenReturn(activeRentals);

        List<RentalResponseDto> retrievedRentals =
                rentalService.getUserRentals(customer, null, true);

        assertNotNull(retrievedRentals);
        assertFalse(retrievedRentals.isEmpty());
        assertEquals(INACTIVE_RENTALS_AMOUNT, retrievedRentals.size());

        verify(rentalRepository).findAllByUserIdAndActive(userId, true);
    }

    @Test
    @DisplayName("setActualReturnDate throws InvalidInputDataException for inactive rental")
    public void setActualReturnDate_InactiveRental_ThrowsException() {
        validRental.setActive(false);
        ActualReturnDateDto requestDto = new ActualReturnDateDto();
        requestDto.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(validRental));

        assertThrows(InvalidInputDataException.class, () ->
                rentalService.setActualReturnDate(RENTAL_ID, requestDto));
    }

    @Test
    @DisplayName("setActualReturnDate updates and returns rental (active to inactive)")
    public void setActualReturnDate_ActiveRental_UpdatesAndReturns() {
        ActualReturnDateDto requestDto = new ActualReturnDateDto();
        requestDto.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(validRental));

        int carInventoryBefore = car.getInventory();
        rentalService.setActualReturnDate(RENTAL_ID, requestDto);

        assertNotNull(validRental.getActualReturnDate());
        assertEquals(carInventoryBefore + INVENTORY_ADJASTMENT, car.getInventory());
        assertFalse(validRental.isActive());

        verify(rentalRepository).save(validRental);
        verify(carRepository).save(car);
    }

    private RentalResponseDto getRentalResponseDto(Rental rental) {
        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setId(RENTAL_ID);
        responseDto.setRentalDate(rental.getRentalDate());
        responseDto.setReturnDate(rental.getReturnDate());
        responseDto.setCar(getCarResponseDto(rental.getCar()));
        return responseDto;
    }

    private RentalRequestDto getRentalCreateDto(Rental rental) {
        RentalRequestDto requestDto = new RentalRequestDto();
        requestDto.setRentalDate(rental.getRentalDate());
        requestDto.setReturnDate(rental.getReturnDate());
        requestDto.setCarId(rental.getCar().getId());
        return requestDto;
    }

    private CarResponseDto getCarResponseDto(Car car) {
        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setId(car.getId());
        carResponseDto.setModel(car.getModel());
        carResponseDto.setBrand(car.getBrand());
        carResponseDto.setType(car.getType());
        carResponseDto.setDailyFee(car.getDailyFee());
        return carResponseDto;
    }

    private List<Rental> generateRentals(boolean active, int amount) {
        List<Rental> rentals = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            User user = new User();
            user.setId(CUSTOMER_ID + i);

            Rental rental = new Rental();
            rental.setRentalDate(LocalDate.now().plusDays(i));
            rental.setReturnDate(LocalDate.now().plusDays(i + ACTIVE_RENTALS_AMOUNT));
            rental.setActualReturnDate(null);
            rental.setActive(active);

            rental.setCar(new Car());
            rental.setUser(user);

            rentals.add(rental);
        }

        return rentals;
    }
}

