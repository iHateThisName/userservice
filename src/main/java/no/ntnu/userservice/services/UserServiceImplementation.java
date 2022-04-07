package no.ntnu.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.repositories.RoleRepository;
import no.ntnu.userservice.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
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
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImplementation implements UserService, UserDetailsService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));

    if (user == null) {
      log.error("User not found in the database");
    } else {
      log.info("User found in the database: {}", username);

    }

    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), authorities);


  }
  @Override
  public User saveUser(User user) {
    log.info("Saving new user {} to the database", user.getName());
    return userRepository.save(user);
  }

  @Override
  public Role saveRole(Role role) {
    log.info("Saving new role {} to the database", role.getName());
    return roleRepository.save(role);
  }

  @Override
  public void addRoleToUser(String username, String roleName) {
    log.info("Adding role {} to user {}", roleName, username);

    User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Username " + username + " not found"));

    Optional<Role> role = roleRepository.findByName(roleName);

    user.getRoles().add(role.orElseThrow());

  }


  @Override
  public User getUsers(String username) {

    log.info("Fetching user {}", username);
    return userRepository.findByUsername(username).orElseThrow();
  }

  @Override
  public List<User> getUsers() {

    log.info("Fetching all users");
    return userRepository.findAll();
  }

}
