package online.carsharing.service.impl;

import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.Type;
import online.carsharing.entity.User;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.user.UserChatIdRepository;
import online.carsharing.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationServiceImplTest {
    private static final LocalDate RETURN_DATE =
            LocalDate.of(2024, 5, 11);
    private static final LocalDate RENTAL_DATE =
            LocalDate.of(2024, 5, 1);
    private static final int NUMBER_OF_INVOCATIONS = 1;
    private static final double CAR_DAILY_FEE = 100.0;
    private static final String CAR_BRAND = "Brand";
    private static final String CAR_MODEL = "Model";
    private static final String EMAIL = "email";
    private static final long USER_ID = 1L;
    private static final long CAR_ID = 1L;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private UserChatIdRepository chatIdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    private Rental rental;
    private User user;
    private Car car;

    @BeforeEach
    public void setUp() {
        car = new Car();
        car.setId(CAR_ID);
        car.setBrand(CAR_BRAND);
        car.setModel(CAR_MODEL);
        car.setType(Type.SUV);
        car.setDailyFee(BigDecimal.valueOf(CAR_DAILY_FEE));

        user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);

        rental = new Rental();
        rental.setRentalDate(RENTAL_DATE);
        rental.setReturnDate(RETURN_DATE);
        rental.setUser(user);
        rental.setCar(car);
    }

    @Test
    @DisplayName("Send notification after new car created")
    void sendNotification_NewCarCreated_OK() {
        NotificationServiceImpl notificationServiceMock =
                Mockito.mock(NotificationServiceImpl.class);
        notificationServiceMock.createCarNotification(car);
        Mockito.verify(
                notificationServiceMock,
                times(NUMBER_OF_INVOCATIONS)).createCarNotification(car);
    }

    @Test
    @DisplayName("Send notification after new rental created")
    void sendNotification_NewRentalCreated_OK() {
        NotificationServiceImpl notificationServiceMock =
                Mockito.mock(NotificationServiceImpl.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
        CarRepository carRepositoryMock = Mockito.mock(CarRepository.class);
        Mockito.when(userRepositoryMock
                .findById(USER_ID))
                .thenReturn(Optional.of(user));
        Mockito.when(carRepositoryMock
                .modelCheckById(CAR_ID))
                .thenReturn("Car Model");
        notificationServiceMock
                .createRentalNotification(rental);
        Mockito.verify(
                notificationServiceMock,
                times(NUMBER_OF_INVOCATIONS))
                .createRentalNotification(rental);
    }
}
