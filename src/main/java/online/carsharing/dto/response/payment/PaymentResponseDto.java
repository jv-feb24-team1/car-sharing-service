package online.carsharing.dto.response.payment;

import java.math.BigDecimal;
import online.carsharing.entity.Payment;

public record PaymentResponseDto(
        Long id,
        Payment.Status status,
        Payment.Type type,
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {
}
