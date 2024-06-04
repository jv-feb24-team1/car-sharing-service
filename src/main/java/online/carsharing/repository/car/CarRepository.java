package online.carsharing.repository.car;

import online.carsharing.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Query("SELECT c.model FROM Car c WHERE c.id = :id")
    String modelCheckById(Long id);
}
