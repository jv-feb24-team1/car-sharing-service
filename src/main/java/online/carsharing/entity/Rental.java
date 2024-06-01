package online.carsharing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@SoftDelete
@RequiredArgsConstructor
@ToString(exclude = {"car", "user"})
@EqualsAndHashCode(exclude = {"car", "user"})
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate rentalDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    private LocalDate actualReturnDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
