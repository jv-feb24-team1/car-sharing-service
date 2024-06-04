package online.carsharing.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponseDto> getPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("user_id") Long userId,
            Pageable pageable) {
        return paymentService.getPayments(userDetails, userId, pageable);
    }

    @PostMapping
    public PaymentResponseDto createPaymentSession(@RequestBody PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @GetMapping("/success")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId) {
        paymentService.markPaymentAsPaid(sessionId);
        return ResponseEntity.ok("Payment successful!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handlePaymentCancel(
            @RequestParam("session_id") String sessionId) {
        paymentService.markPaymentAsCanceled(sessionId);
        return ResponseEntity.ok("Payment process canceled.");
    }
}
