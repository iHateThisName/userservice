package no.ntnu.userservice.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
// @Entity is to tell spring boot that this class is entity in the db.
// @Getter is lombok dependency that creates getters.
// @Setter is lombok that creates setters.
// @NoArgsConstructor is lombok and creates a constructor with no argument.
// @AllArgsConstructor is lombok and creates a constructor with all the argument.
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class User {

    //@Id tells that the attribute below is id in the db table
    //@GeneratedValue tells the db how to generate this id
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    //Eager makes it load all the roles whenever it loads the user
    //so when it loads the user it will also load all the roles in the db
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();


}
