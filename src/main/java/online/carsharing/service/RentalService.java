package online.carsharing.service;

import java.util.List;
import online.carsharing.dto.request.rental.ActualReturnDateDto;
import online.carsharing.dto.request.rental.RentalRequestDto;
import online.carsharing.dto.response.rental.RentalResponseDto;
import online.carsharing.entity.User;

public interface RentalService {
    RentalResponseDto save(User user, RentalRequestDto rentalDto);

    RentalResponseDto getById(Long rentalId);

    List<RentalResponseDto> getUserRentals(User user, Long userId, boolean isActive);

    RentalResponseDto setActualReturnDate(Long rentalId, ActualReturnDateDto actualDate);
}
