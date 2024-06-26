package online.carsharing.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.NotificationScheduler;
import online.carsharing.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class NotificationSchedulerImplTest {
    private static final LocalDate RENTAL_DATE = LocalDate.ofEpochDay(2024 - 05 - 01);
    private static final LocalDate RETURN_DATE = LocalDate.ofEpochDay(2024 - 05 - 11);
    private static final String CAR_MODEL = "Model";
    private static final String EMAIL = "email";
    private static final int ADD_DAY = 1;

    @InjectMocks
    private NotificationSchedulerImpl scheduler;

    private NotificationScheduler notificationScheduler;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Spy
    private List<Rental> mockedOverdueRentals = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Send notification in the morning if there is no overdue rental")
    public void sendMorningNotification_NoOverdueRentals_OK() {
        mockedOverdueRentals.clear();
        LocalDate tomorrow = LocalDate.now().plusDays(ADD_DAY);
        when(rentalRepository.findOverdueRentals(tomorrow)).thenReturn(mockedOverdueRentals);
        scheduler.morningReminder();
        verify(notificationService).sendNotification("No rentals overdue today!");
    }

    @Test
    @DisplayName("Send notification if overdue rental exist")
    void sendEveningNotification_OverdueRentalExist_OK() {
        Car car = createCar();
        User user = createUser();
        Rental rental = new Rental();
        rental.setRentalDate(RENTAL_DATE);
        rental.setReturnDate(RETURN_DATE);
        rental.setCar(car);
        rental.setUser(user);
        mockedOverdueRentals.add(rental);
        LocalDate tomorrow = LocalDate.now().plusDays(ADD_DAY);
        when(rentalRepository.findOverdueRentals(tomorrow)).thenReturn(mockedOverdueRentals);
        scheduler.eveningReminder();
        verify(notificationService).sendNotification(String.format(
                "Warning overdue rentals detected!",
                rental.getRentalDate(),
                rental.getReturnDate(),
                car.getModel(),
                user.getEmail()
        ));
        verify(notificationService).sendNotification("Total rentals overdue: "
                + mockedOverdueRentals.size());
    }

    private Car createCar() {
        Car car = new Car();
        car.setModel(CAR_MODEL);
        return car;
    }

    private User createUser() {
        User user = new User();
        user.setEmail(EMAIL);
        return user;
    }
}
