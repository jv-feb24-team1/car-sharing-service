package online.carsharing.repository.stubs;

import online.carsharing.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentStubRepository extends JpaRepository<Rental, Long> {
}
