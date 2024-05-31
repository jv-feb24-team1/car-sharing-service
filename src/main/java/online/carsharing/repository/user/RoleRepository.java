package online.carsharing.repository.user;

import java.util.Optional;
import online.carsharing.entity.Role;
import online.carsharing.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
