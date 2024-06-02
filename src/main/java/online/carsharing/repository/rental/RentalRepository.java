package online.carsharing.repository.rental;

import java.time.LocalDate;
import java.util.List;
import online.carsharing.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId AND r.active = :isActive")
    List<Rental> findAllByUserIdAndActive(Long userId, Boolean isActive);

    @Query("SELECT r FROM Rental r LEFT JOIN FETCH r.car LEFT JOIN FETCH"
            + " r.user WHERE r.returnDate <= :tomorrow AND r.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(@Param("tomorrow") LocalDate tomorrow);
}
