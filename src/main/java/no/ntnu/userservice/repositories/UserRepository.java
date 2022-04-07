package no.ntnu.userservice.repositories;

import no.ntnu.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
