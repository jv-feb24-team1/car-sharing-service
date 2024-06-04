package online.carsharing.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Rental;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.NotificationScheduler;
import online.carsharing.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerImpl implements NotificationScheduler {
    private static final String EXPIRED_RENTALS =
            "Overdue rental! \nRental Date: %s\nReturn Date: %s\nCar: %s\nUser: %s";
    private static final String NO_RENTALS =
            "No rentals overdue today!";
    private static final String TIME_MORNING_REMINDER =
            "0 30 8 * * ?";
    private static final String TIME_EVENING_REMINDER =
            "0 30 16 * * ?";
    private static final String RENTAL_WARNING_TG_HEADER =
            "Warning overdue rentals detected!";
    private static final String RENTAL_WARNING_TG_TOTAL_OVERDUE =
            "Total rentals overdue: ";
    private static final String NO_CAR_WITH_ID =
            "Unknown Car";
    private static final String NO_USER_WITH_ID =
            "Unknown User";
    private static final int DAY = 1;

    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Override
    @Async
    @Scheduled(cron = TIME_MORNING_REMINDER)
    public void morningReminder() {
        rentalReminder();
    }

    @Override
    @Async
    @Scheduled(cron = TIME_EVENING_REMINDER)
    public void eveningReminder() {
        rentalReminder();
    }

    private void rentalReminder() {
        LocalDate tomorrow = LocalDate.now().plusDays(DAY);
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(tomorrow);

        if (overdueRentals.isEmpty()) {
            notificationService.sendNotification(NO_RENTALS);
        } else {
            notificationService.sendNotification(RENTAL_WARNING_TG_HEADER);
            for (Rental rental : overdueRentals) {
                notificationService.sendNotification(createRentalOverdueMessage(rental));
            }
            notificationService.sendNotification(RENTAL_WARNING_TG_TOTAL_OVERDUE
                    + overdueRentals.size());
        }
    }

    private String searchOverdueRentalCarModel(Rental rental) {
        return carRepository.findById(rental
                        .getCar()
                        .getId())
                .map(car -> car.getModel())
                .orElse(NO_CAR_WITH_ID);
    }

    private String searchOverdueRentalUserEmail(Rental rental) {
        return userRepository.findById(rental
                        .getUser()
                        .getId())
                .map(user -> user.getEmail())
                .orElse(NO_USER_WITH_ID);
    }

    private String createRentalOverdueMessage(Rental rental) {
        return String.format(
                EXPIRED_RENTALS,
                rental.getRentalDate(),
                rental.getReturnDate(),
                searchOverdueRentalCarModel(rental),
                searchOverdueRentalUserEmail(rental)
        );
    }
}
