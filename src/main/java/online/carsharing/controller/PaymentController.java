package online.carsharing.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponseDto> getAllPayments(
            @AuthenticationPrincipal UserDetails user, Pageable pageable) {
        return paymentService.getAllPayments(user, pageable);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{userId}")
    public List<PaymentResponseDto> getAllPaymentsByUserId(
            @PathVariable Long userId, Pageable pageable) {
        return paymentService.getAllPaymentsByUserId(userId, pageable);
    }

    @PostMapping
    public PaymentResponseDto createPaymentSession(@RequestBody PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @GetMapping("/success")
    public ResponseEntity<String> handlePaymentSuccess(@RequestParam String sessionId) {
        paymentService.markPaymentAsPaid(sessionId);
        return ResponseEntity.ok("Payment successful!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handlePaymentCancel(@RequestParam String sessionId) {
        paymentService.markPaymentAsCanceled(sessionId);
        return ResponseEntity.ok("Payment process canceled.");
    }
}
