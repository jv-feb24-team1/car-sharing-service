package online.carsharing.controller;

import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponseDto> createRental(
            @RequestBody RentalRequestDto rentalRequestDto) {
        RentalResponseDto createdRental = rentalService.save(rentalRequestDto);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }
}
