package online.carsharing.dto.request.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import online.carsharing.entity.Payment;

public record PaymentRequestDto(
        @NotNull(message = "Rental id can't be null")
        @Min(1L)
        Long rentalId,
        @NotNull(message = "Payment type can't be null")
        Payment.Type type) {
}
