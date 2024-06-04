package online.carsharing.repository.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import online.carsharing.entity.Rental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    public static final String TEST_DATABASE = "classpath:db.script/rental-test-db.sql";
    private static final long NO_RENTALS_USER_ID = 4L;
    private static final long USER_ID = 1L;
    private static final int EXPECTED_SIZE = 1;

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Find all active rentals for a user with active rental")
    @Sql(scripts = {TEST_DATABASE}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByUserIdAndActive_ValidUserId_ReturnsRentals() {
        List<Rental> rentals =
                rentalRepository.findAllByUserIdAndActive(USER_ID, true);

        assertNotNull(rentals);
        assertFalse(rentals.isEmpty());
        assertEquals(EXPECTED_SIZE, rentals.size());
        assertTrue(rentals.getFirst().isActive());
    }

    @Test
    @DisplayName("Find all active rentals for a user without any rentals")
    @Sql(scripts = {TEST_DATABASE}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByUserIdAndActive_ValidUserIdNoActiveRentals_ReturnsEmptyList() {
        List<Rental> rentals = rentalRepository.findAllByUserIdAndActive(NO_RENTALS_USER_ID, true);

        assertNotNull(rentals);
        assertTrue(rentals.isEmpty());
    }

    @Test
    @DisplayName("Find all active rentals in database")
    @Sql(scripts = {TEST_DATABASE}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByActive_ActiveRentals_ReturnsRentals() {
        List<Rental> rentals = rentalRepository.findAllByActive(true);

        assertNotNull(rentals);
        assertFalse(rentals.isEmpty());
        rentals.forEach(r -> assertTrue(r.isActive()));
    }

    @Test
    @DisplayName("Find all inactive rentals in database")
    @Sql(scripts = {TEST_DATABASE}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllByActive_NoActiveRentals_ReturnsEmptyList() {
        List<Rental> rentals = rentalRepository.findAllByActive(false);

        assertNotNull(rentals);
        assertFalse(rentals.isEmpty());
        rentals.forEach(r -> assertFalse(r.isActive()));
    }

    @Test
    @DisplayName("Find overdue rentals with actual return date null")
    @Sql(scripts = {TEST_DATABASE}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findOverdueRentals_OverdueRentalsExist_ReturnsRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());

        assertNotNull(overdueRentals);
        assertFalse(overdueRentals.isEmpty());
        assertEquals(EXPECTED_SIZE, overdueRentals.size());

        Rental overdueRental = overdueRentals.getFirst();
        assertTrue(overdueRental.getReturnDate().isBefore(LocalDate.now()));
        assertNull(overdueRental.getActualReturnDate());
    }
}
