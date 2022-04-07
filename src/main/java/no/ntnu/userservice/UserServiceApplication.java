package no.ntnu.userservice;

import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }

  /**
   * This method is called to decide what encryption to use for password checking
   *
   * @return The password encryptor
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  CommandLineRunner run(UserService userService) {
    return args -> {

      userService.saveRole(new Role(null, "ROLE_USER"));
      userService.saveRole(new Role(null, "ROLE_MANAGER"));
      userService.saveRole(new Role(null, "ROLE_ADMIN"));

      userService.saveUser(
          new User(
              null,
              "John Travolta",
              "john",
              "1234",
              new ArrayList<>()));

      userService.saveUser(
          new User(
              null,
              "Will Smith",
              "will",
              "1234",
              new ArrayList<>()));

      userService.saveUser(
          new User(
              null,
              "Jim Carry",
              "jim",
              "1234",
              new ArrayList<>()));

      userService.saveUser(
          new User(
              null,
              "Arnold Schwarzenegger",
              "arnold",
              "1234",
              new ArrayList<>()));

      userService.addRoleToUser("john", "ROLE_USER");
      userService.addRoleToUser("john", "ROLE_MANAGER");
      userService.addRoleToUser("will", "ROLE_MANAGER");
      userService.addRoleToUser("jim", "ROLE_ADMIN");
      userService.addRoleToUser("arnold", "ROLE_ADMIN");
      userService.addRoleToUser("arnold", "ROLE_USER");
    };
  }

}
