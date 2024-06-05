package online.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.entity.User;
import online.carsharing.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Controller", description = "API for managing payments, including operations "
        + "for creating payments, retrieving payments information.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Get Payments",
            description = "Retrieves all payments information of "
                    + "the currently authenticated user by their ID."
                    + " Customers can only see their payments, while managers can see all of them."
                    + " Managers also can receive all payments of a specific user."
    )
    @GetMapping
    public List<PaymentResponseDto> getPayments(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long userId,
            Pageable pageable) {
        return paymentService.getPayments(user.getId(), userId, pageable);
    }

    @Operation(
            summary = "Create payment session",
            description = "Create payment session by rental Id and payment type."
                    + " Session lifetime 24 hours."
    )
    @PostMapping
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid PaymentRequestDto requestDto, @AuthenticationPrincipal User user) {
        return paymentService.createPaymentSession(requestDto, user.getId());
    }

    @Operation(
            summary = "Successful payment",
            description = "Endpoint for stripe redirection"
    )
    @GetMapping("/success")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId) {
        paymentService.markPaymentAsPaid(sessionId);
        return ResponseEntity.ok("Payment successful!");
    }

    @Operation(
            summary = "Canceled payment",
            description = "Endpoint for stripe redirection. "
                    + "Stripe will automatically cancel the payment after 24 hours"
    )
    @GetMapping("/cancel")
    public ResponseEntity<String> handlePaymentCancel(
            @RequestParam("session_id") String sessionId) {
        paymentService.markPaymentAsCanceled(sessionId);
        return ResponseEntity.ok("Payment process canceled.");
    }
}
