package online.carsharing.dto.request.payment;

import online.carsharing.entity.Payment;

public record PaymentRequestDto(
        Long rentalId,
        Payment.Type type) {
}
