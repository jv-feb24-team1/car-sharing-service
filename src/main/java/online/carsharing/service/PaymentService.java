package online.carsharing.service;

import java.util.List;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.entity.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface PaymentService {

    List<PaymentResponseDto> getAllPayments(UserDetails user, Pageable pageable);

    List<PaymentResponseDto> getAllPaymentsByUserId(Long userId, Pageable pageable);

    PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto);

    void markPaymentAsPaid(String sessionId);

    void markPaymentAsCanceled(String sessionId);

    void createOverduePaymentIfNecessary(Rental rental);
}
