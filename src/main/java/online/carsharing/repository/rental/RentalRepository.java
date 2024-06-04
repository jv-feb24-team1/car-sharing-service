package online.carsharing.repository.rental;

import java.util.List;
import java.util.Optional;
import online.carsharing.entity.Payment;
import online.carsharing.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r JOIN FETCH r.car "
            + "WHERE r.user.id = :userId AND r.active = :isActive")
    List<Rental> findAllByUserIdAndActive(Long userId, Boolean isActive);

    @Query("SELECT r FROM Rental r JOIN FETCH r.user JOIN FETCH r.car WHERE r.active = :isActive")
    List<Rental> findAllByActive(boolean isActive);

    @Query("SELECT p FROM Payment p JOIN Rental r ON p.rentalId = r.id WHERE r.user.id = :userId")
    Page<Payment> findAllPaymentsByUserId(Long userId, Pageable pageable);

    @Query("SELECT r FROM Rental r JOIN FETCH r.car WHERE r.id = :rentalId")
    Optional<Rental> findByIdWithCar(Long rentalId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Rental r "
            + "WHERE r.user.id = :userId AND EXISTS "
            + "(SELECT p FROM Payment p WHERE p.rentalId = r.id AND p.status = 'PENDING')")
    boolean existsPendingPaymentsByUserId(Long userId);
}
