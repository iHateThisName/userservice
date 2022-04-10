package no.ntnu.userservice.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String authorizationHeader = request.getHeader(AUTHORIZATION);

    //This if statement will be true if the request is of the type authorization header
    // && it starts with "Bearer ".
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

      try {

        //want the string that is after "Bearer ".
        String refreshToken = authorizationHeader.substring("Bearer ".length());

        //todo make a utility class for this.
        //Need the algorithm to read the refreshToken.
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        //need a verifier to verify and decode the refreshToken
        JWTVerifier verifier = JWT.require(algorithm).build();

        //decoding the refreshToken to make it readable as well as verifying it.
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        //retrieving date from the refreshToken
        String username = decodedJWT.getSubject();

        //Checks if the user exist in the database
        User user = userService.getUsers(username);

        String accessToken = JWT.create()
            .withSubject(user.getUsername())
            //expire date for the access refreshToken.
            .withExpiresAt(java.sql.Date.valueOf(LocalDate.now().plusWeeks(1)))
            //the issuer is the name of the author of this refreshToken
            .withIssuer(request.getRequestURL().toString())
            //
            .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);


      } catch (Exception exception) {

        response.setHeader("error ", exception.getMessage());
        response.setStatus(FORBIDDEN.value());

        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", exception.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);

      }

    } else {
      //so if the request do not have an authorization header
      //we are just going to let the request continue.

      throw new RuntimeException("Refresh token is missing");
    }


  }


  @Data
  static
  class RoleToUserForm {
    private String username;
    private String roleName;
  }
}
