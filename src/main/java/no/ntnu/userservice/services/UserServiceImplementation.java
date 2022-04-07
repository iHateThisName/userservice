package no.ntnu.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.repositories.RoleRepository;
import no.ntnu.userservice.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */

// @Service tells spring that this is a service class.
// @RequiredArgsConstructor from lombok and creates a constructor with the required arguments.
// @Transactional tells spring that everything in this class is transactional.
// @Slf4j a logger
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImplementation implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) throws RoleNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Username " + username + " not found"));

        Role newRole = roleRepository
                .findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(
                        "Role " + roleName + " not found"
                ));

        if (user.getRoles()
                .stream()
                .noneMatch(role -> role.equals(newRole))) {

            user.getRoles().add(newRole);
        }
    }

    @Override
    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
