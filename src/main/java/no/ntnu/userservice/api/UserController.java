package no.ntnu.userservice.api;

import lombok.RequiredArgsConstructor;
import no.ntnu.userservice.models.User;
import no.ntnu.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        //Creating a responseEntity witch is what the browser receives
        //the .ok tells the user it is code 200
        //all the is place in the body
        return ResponseEntity.ok().body(userService.getUsers());
    }
}
