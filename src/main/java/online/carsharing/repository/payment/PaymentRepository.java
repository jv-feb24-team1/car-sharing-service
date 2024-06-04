package online.carsharing.repository.payment;

import online.carsharing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findBySessionId(String sessionId);
}
