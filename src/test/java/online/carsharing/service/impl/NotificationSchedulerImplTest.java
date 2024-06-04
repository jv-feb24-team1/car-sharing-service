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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class NotificationSchedulerImplTest {
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
    public void sendMorningNotification_NoOverdueRentals_OK() {
        mockedOverdueRentals.clear();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(rentalRepository.findOverdueRentals(tomorrow)).thenReturn(mockedOverdueRentals);

        scheduler.morningReminder();

        verify(notificationService).sendNotification("No rentals overdue today!");
    }

    @Test
    public void sendEveningNotification_NoOverdueRentals_OK() {
        mockedOverdueRentals.clear();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(rentalRepository.findOverdueRentals(tomorrow)).thenReturn(mockedOverdueRentals);

        scheduler.eveningReminder();

        verify(notificationService).sendNotification("No rentals overdue today!");
    }

    @Test
    void sendEveningNotification_OverdueRentalExist_OK() {
        Car car = createCar();
        User user = createUser();

        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.ofEpochDay(2024 - 05 - 01));
        rental.setReturnDate(LocalDate.ofEpochDay(2024 - 05 - 11));
        rental.setCar(car);
        rental.setUser(user);
        mockedOverdueRentals.add(rental);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(rentalRepository.findOverdueRentals(tomorrow)).thenReturn(mockedOverdueRentals);

        scheduler.eveningReminder();

        verify(notificationService).sendNotification(String.format(
                "Warning overdue rentals detected!",
                rental.getRentalDate(),
                rental.getReturnDate(),
                car.getModel(),
                user.getEmail()
        ));
        verify(notificationService).sendNotification("Total rentals overdue: 1");
    }

    private Car createCar() {
        Car car = new Car();
        car.setModel("Model");
        return car;
    }

    private User createUser() {
        User user = new User();
        user.setEmail("email");
        return user;
    }
}
