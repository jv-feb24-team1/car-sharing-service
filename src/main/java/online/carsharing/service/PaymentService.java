package online.carsharing.service;

import java.util.List;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.entity.Rental;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    List<PaymentResponseDto> getPayments(
            Long userId, Long requestParamUserId, Pageable pageable);

    PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto, Long userId);

    void markPaymentAsPaid(String sessionId);

    void markPaymentAsCanceled(String sessionId);

    void createOverduePayment(Rental rental);
}
