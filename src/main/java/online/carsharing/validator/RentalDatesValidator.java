package online.carsharing.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import online.carsharing.annotation.ValidRentalDates;
import online.carsharing.dto.request.rental.RentalRequestDto;

public class RentalDatesValidator implements
        ConstraintValidator<ValidRentalDates, RentalRequestDto> {

    @Override
    public boolean isValid(RentalRequestDto rentalRequestDto, ConstraintValidatorContext context) {
        return rentalRequestDto.getReturnDate().isAfter(rentalRequestDto.getRentalDate());
    }
}
