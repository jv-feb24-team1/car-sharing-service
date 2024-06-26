package online.carsharing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private Long rentalId;

    private String sessionUrl;

    private String sessionId;

    @DecimalMin(value = "0.5")
    private BigDecimal amountToPay;

    public enum Status {
        PENDING, PAID, EXPIRED, CANCELLED
    }

    public enum Type {
        PAYMENT, FINE
    }
}
