package online.carsharing.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import online.carsharing.validator.RentalDatesValidator;

@Constraint(validatedBy = RentalDatesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRentalDates {
    String message() default "Return date must be after rental date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
