package no.ntnu.userservice.services;

import no.ntnu.userservice.models.Role;
import no.ntnu.userservice.models.User;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
public interface UserService {

    User saveUser(User user);
    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUsers(String username);
    List<User> getUsers();
}
