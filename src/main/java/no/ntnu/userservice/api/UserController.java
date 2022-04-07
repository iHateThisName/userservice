package no.ntnu.userservice.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers() {
    //Creating a responseEntity witch is what the browser receives
    //the .ok tells the user it is code 200
    //all the is place in the body
    return ResponseEntity.ok().body(userService.getUsers());
  }

  @PostMapping("/user/save")
  public ResponseEntity<User> saveUser(@RequestBody User user) {
    //.created will response with 201
    //Which means a resource was created in the server.
    URI uri = URI.create(
        ServletUriComponentsBuilder
            .fromCurrentContextPath().path("/api/user/save").toUriString());
    return ResponseEntity.created(uri).body(userService.saveUser(user));
  }

  @PostMapping("/role/save")
  public ResponseEntity<Role> saveRole(@RequestBody Role role) {
    //.created will response with 201
    //Which means a resource was created in the server.
    URI uri = URI.create(
        ServletUriComponentsBuilder
            .fromCurrentContextPath().path("/api/role/save").toUriString());

    return ResponseEntity.created(uri).body(userService.saveRole(role));
  }

  @PostMapping("/role/addtouser")
  public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {

    userService.addRoleToUser(form.getUsername(), form.getRoleName());
    return ResponseEntity.ok().build();
  }


  @Data
  static
  class RoleToUserForm {
    private String username;
    private String roleName;
  }
}
