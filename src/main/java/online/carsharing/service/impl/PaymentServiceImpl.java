package online.carsharing.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.dto.request.payment.PaymentRequestDto;
import online.carsharing.dto.response.payment.PaymentResponseDto;
import online.carsharing.entity.Car;
import online.carsharing.entity.Payment;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.exception.EntityNotFoundException;
import online.carsharing.exception.PaymentProcessingException;
import online.carsharing.mapper.PaymentMapper;
import online.carsharing.repository.payment.PaymentRepository;
import online.carsharing.repository.rental.RentalRepository;
import online.carsharing.repository.user.UserRepository;
import online.carsharing.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final String PAYMENT_NAME = "Rental Payment for car";
    private static final String CURRENCY = "USD";
    private static final Long RENT_QUANTITY = 1L;
    private static final Long CENTS_IN_DOLLAR = 100L;
    private static final String ROLE_MANAGER = "ROLE_MANAGER";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Override
    public List<PaymentResponseDto> getPayments(
            UserDetails userDetails, Long userId, Pageable pageable) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with username: " + userDetails.getUsername()));
        if (isManager(user)) {
            if (userId == null) {
                return paymentRepository.findAll(pageable).getContent().stream()
                        .map(paymentMapper::toDto)
                        .toList();
            } else if (userRepository.existsById(userId)) {
                return getAllDtoPaymentsByUserId(userId, pageable);
            } else {
                throw new EntityNotFoundException("User not found with id: " + userId);
            }
        }
        return getAllDtoPaymentsByUserId(user.getId(), pageable);
    }

    @Override
    public void createOverduePayment(Rental rental) {
            BigDecimal overdueAmount = calculateAmount(
                    Payment.Type.FINE, rental, rental.getCar().getDailyFee());
            createAndSavePayment(overdueAmount, rental, Payment.Type.FINE);
    }

    @Override
    public PaymentResponseDto createPaymentSession(PaymentRequestDto requestDto) {
        Payment paymentEntity = paymentMapper.toEntity(requestDto);
        Rental rental = rentalRepository.findByIdWithCar(paymentEntity.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental not found with id: " + paymentEntity.getRentalId()));

        if (rentalRepository.existsPendingPaymentsByUserId(rental.getUser().getId())) {
            throw new PaymentProcessingException(
                    "User already have active rentals or pending payments");
        }

        if (rental.getCar() == null) {
            throw new EntityNotFoundException("Car not found with id: " + rental.getCar().getId());
        }

        BigDecimal amount = calculateAmount(
                paymentEntity.getType(), rental, rental.getCar().getDailyFee());

        Payment payment = createAndSavePayment(amount, rental, paymentEntity.getType());

        return paymentMapper.toDto(payment);
    }

    @Override
    public void markPaymentAsPaid(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId);
        if (payment != null) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
        }
    }

    @Override
    public void markPaymentAsCanceled(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId);
        if (payment != null) {
            payment.setStatus(Payment.Status.CANCELLED);
            paymentRepository.save(payment);
        }
    }

    private Session getSession(BigDecimal amount, Car car) {
        try {
            return Session.create(getSessionParams(amount, car));
        } catch (StripeException e) {
            throw new PaymentProcessingException("Stripe API exception", e);
        }
    }

    private SessionCreateParams.LineItem.PriceData.ProductData getProductData(Car car) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(PAYMENT_NAME
                        + " brand: " + car.getBrand()
                        + " model: " + car.getModel()
                        + " type: " + car.getType())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData getPriceData(BigDecimal amount, Car car) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(CURRENCY)
                .setUnitAmount(amount.multiply(
                        BigDecimal.valueOf(CENTS_IN_DOLLAR)).longValue())
                .setProductData(getProductData(car))
                .build();
    }

    private SessionCreateParams.LineItem getLineItem(BigDecimal amount, Car car) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(RENT_QUANTITY)
                .setPriceData(getPriceData(amount, car))
                .build();
    }

    private SessionCreateParams getSessionParams(BigDecimal amount, Car car) {
        return SessionCreateParams.builder()
                .addLineItem(getLineItem(amount, car))
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "{CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl + "{CHECKOUT_SESSION_ID}")
                .build();
    }

    private BigDecimal calculateAmount(Payment.Type type, Rental rental, BigDecimal dailyFee) {
        BigDecimal totalAmount;
        long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        totalAmount = dailyFee.multiply(BigDecimal.valueOf(rentalDays));

        if (type == Payment.Type.FINE
                && rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            long overdueDays = ChronoUnit.DAYS.between(
                    rental.getReturnDate(), rental.getActualReturnDate());
            BigDecimal fineAmount = dailyFee.multiply(
                    BigDecimal.valueOf(overdueDays)).multiply(FINE_MULTIPLIER);
            totalAmount = totalAmount.add(fineAmount);
        }
        return totalAmount;
    }

    private List<PaymentResponseDto> getAllDtoPaymentsByUserId(Long userId, Pageable pageable) {
        return rentalRepository.findAllPaymentsByUserId(userId, pageable).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private Payment createAndSavePayment(BigDecimal amount, Rental rental, Payment.Type type) {
        Session session = getSession(amount, rental.getCar());

        Payment payment = new Payment();
        payment.setRentalId(rental.getId());
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(type);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmountToPay(amount);

        return paymentRepository.save(payment);
    }

    private boolean isManager(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(ROLE_MANAGER));
    }
}
