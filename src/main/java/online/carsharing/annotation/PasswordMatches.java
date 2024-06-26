package online.carsharing.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import online.carsharing.validator.PasswordMatchesValidator;
import online.carsharing.validator.PasswordMatchesValidatorForUpdate;

@Constraint(validatedBy = {PasswordMatchesValidator.class, PasswordMatchesValidatorForUpdate.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
