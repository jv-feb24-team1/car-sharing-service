package online.carsharing.controller.stubs;

import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Rental;
import online.carsharing.service.stubs.RentalStubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stub/rentals")
@RequiredArgsConstructor
public class RentStubController {
    private final RentalStubService rentalStubService;

    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        Rental createdRental = rentalStubService.createRental(rental);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }
}
