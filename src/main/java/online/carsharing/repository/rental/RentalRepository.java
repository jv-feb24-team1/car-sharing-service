package online.carsharing.repository.rental;

import java.util.List;
import online.carsharing.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId AND r.active = :isActive")
    List<Rental> findAllByUserIdAndActive(Long userId, Boolean isActive);
}
