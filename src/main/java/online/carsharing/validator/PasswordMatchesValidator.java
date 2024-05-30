package online.carsharing.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import online.carsharing.annotation.PasswordMatches;
import online.carsharing.dto.request.user.UserRegisterRequestDto;

public class PasswordMatchesValidator implements
        ConstraintValidator<PasswordMatches, UserRegisterRequestDto> {

    @Override
    public boolean isValid(UserRegisterRequestDto user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
