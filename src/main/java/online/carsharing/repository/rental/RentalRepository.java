package online.carsharing.repository.rental;

import java.time.LocalDate;
import java.util.List;
import online.carsharing.entity.Rental;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @EntityGraph(attributePaths = {"car", "user"})
    @Query("SELECT r FROM Rental r WHERE r.returnDate <= :tomorrow AND r.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(LocalDate tomorrow);
}
