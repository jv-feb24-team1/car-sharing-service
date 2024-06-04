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
    public static final double CAR_DAILY_FEE = 100.0;
    public static final String CAR_BRAND = "Brand";
    public static final String CAR_MODEL = "Model";
    public static final String EMAIL = "email";
    public static final long USER_ID = 1L;
    public static final long CAR_ID = 1L;

    @Mock
    private UserChatIdRepository chatIdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

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
        rental.setRentalDate(LocalDate.of(2024, 5, 1));
        rental.setReturnDate(LocalDate.of(2024, 5, 11));
        rental.setUser(user);
        rental.setCar(car);
    }

    @Test
    @DisplayName("Send notification after new car created")
    void sendNotification_NewCarCreated_OK() {
        NotificationServiceImpl notificationServiceMock =
                Mockito.mock(NotificationServiceImpl.class);
        notificationServiceMock.createCarNotification(car);
        Mockito.verify(notificationServiceMock,
                times(1)).createCarNotification(car);
    }

    @Test
    @DisplayName("Send notification after new rental created")
    void sendNotification_NewRentalCreated_OK() {
        NotificationServiceImpl notificationServiceMock =
                Mockito.mock(NotificationServiceImpl.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
        CarRepository carRepositoryMock = Mockito.mock(CarRepository.class);
        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(carRepositoryMock.modelCheckById(1L)).thenReturn("Car Model");
        notificationServiceMock.createRentalNotification(rental);
        Mockito.verify(notificationServiceMock,
                times(1)).createRentalNotification(rental);
    }
}
