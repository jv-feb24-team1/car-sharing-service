package online.carsharing.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.Rental;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.NotificationScheduler;
import online.carsharing.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerImp implements NotificationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerImp.class);
    private static final String EXPIRED_RENTALS =
            "Overdue rental! \nRental Date: %s\nReturn Date: %s\nCar: %s\nUser: %s";
    private static final String NO_RENTALS =
            "No rentals overdue today!";

    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Override
    @Async
    @Scheduled(cron = "0 55 3 * * ?")
    public void morningReminder() {
        rentalReminder();
    }

    @Override
    @Async
    @Scheduled(cron = "0 30 16 * * ?")
    public void eveningReminder() {
        rentalReminder();
    }

    private void rentalReminder() {
        LocalDate today = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(today);

        List<RentalResponseDto> overdueRentalDtos = overdueRentals.stream()
                .map(rental -> {
                    RentalResponseDto dto = new RentalResponseDto();
                    dto.setId(rental.getId());
                    dto.setRentalDate(rental.getRentalDate());
                    dto.setReturnDate(rental.getReturnDate());
                    dto.setActualReturnDate(rental.getActualReturnDate());
                    dto.setCarId(rental.getCar().getId());
                    dto.setUserId(rental.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        if (overdueRentalDtos.isEmpty()) {
            notificationService.sendNotification(NO_RENTALS);
        } else {
            notificationService.sendNotification("Warning overdue rentals detected!");
            for (RentalResponseDto dto : overdueRentalDtos) {
                String carModel = carRepository.findById(dto.getCarId())
                        .map(car -> car.getModel())
                        .orElse("Unknown Car");
                String userEmail = userRepository.findById(dto.getUserId())
                        .map(user -> user.getEmail())
                        .orElse("Unknown User");
                String message = String.format(EXPIRED_RENTALS,
                        dto.getRentalDate(), dto.getReturnDate(),
                        carModel, userEmail);
                notificationService.sendNotification(message);
            }
            notificationService.sendNotification("Total rentals overdue: "
                    + overdueRentalDtos.size());
        }

        logger.info("Checked for overdue rentals. Total found: " + overdueRentalDtos.size());
    }
}
