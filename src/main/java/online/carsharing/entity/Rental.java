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
@ToString(exclude = {"carId", "userId"})
@EqualsAndHashCode(exclude = {"carId", "userId"})
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "rental_date")
    private LocalDate rentalDate;

    @Column(nullable = false, name = "return_date")
    private LocalDate returnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Car carId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User userId;
}
