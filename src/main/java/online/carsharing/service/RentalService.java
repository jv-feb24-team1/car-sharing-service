package online.carsharing.service;

import java.time.LocalDate;
import java.util.List;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;

public interface RentalService {
    RentalResponseDto save(Long userId, RentalRequestDto rentalDto);

    RentalResponseDto getById(Long rentalId);

    List<RentalResponseDto> getByUserId(Long userId);

    List<RentalResponseDto> findAll();

    RentalRequestDto setActualReturnDate(Long rentalId, LocalDate actualDate);

    void delete(Long id);
}
