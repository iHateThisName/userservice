package no.ntnu.userservice.repositories;

import no.ntnu.userservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
