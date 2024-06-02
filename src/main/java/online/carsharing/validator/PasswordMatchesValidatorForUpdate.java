package online.carsharing.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import online.carsharing.annotation.PasswordMatches;
import online.carsharing.dto.request.user.UserUpdateRequestDto;

public class PasswordMatchesValidatorForUpdate implements
        ConstraintValidator<PasswordMatches, UserUpdateRequestDto> {
    @Override
    public boolean isValid(UserUpdateRequestDto user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
