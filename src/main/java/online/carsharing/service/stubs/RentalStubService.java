package online.carsharing.service.stubs;

import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Rental;
import online.carsharing.repository.stubs.RentStubRepository;
import online.carsharing.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalStubService {
    private final RentStubRepository rentStubRepository;
    private final NotificationService notificationService;

    public Rental createRental(Rental rental) {
        notificationService.rentalCreation(rental);
        return rentStubRepository.save(rental);
    }
}
