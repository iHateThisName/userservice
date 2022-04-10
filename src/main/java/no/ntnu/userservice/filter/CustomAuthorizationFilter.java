package no.ntnu.userservice.filter;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 *
 * This class will check if the token provide from the user is correct.
 * The OncePerRequestFilter is going to intercept every request that comes into the application.
 */
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    //These are the request that we are not going to check the tokens on.
    if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh"))  {
      //we are just passing it along, as if the user has valid permissions.
      filterChain.doFilter(request, response);
    } else {

      //We only retrieve the date from the header with the type authorization
      //so if this is null then there was non information in the request's authorization header
      String authorizationHeader = request.getHeader(AUTHORIZATION);

      //This if statement will be true if the request is of the type authorization header
      // && it starts with "Bearer ".
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

        try {

          //want the string that is after "Bearer ".
          String token = authorizationHeader.substring("Bearer ".length());

          //todo make a utility class for this.
          //Need the algorithm to read the token.
          Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

          //need a verifier to verify and decode the token
          JWTVerifier verifier = JWT.require(algorithm).build();

          //decoding the token to make it readable as well as verifying it.
          DecodedJWT decodedJWT = verifier.verify(token);

          //retrieving date from the token
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

          //Need to convert the string roles from the token to a SimpleGrantedAuthority
          //and have that in a Collection/List
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
          });

          // This is how we tell spring that this is a user
          // here we have their username and their roles and that tells spring what they can to in the application
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(username, null, authorities);

          // Here spring is going to look at the user, look at their role, and determine what resource they can access
          // and what they can access, depending on the roles.
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);

          //Going to call the filer chain, because we still need to let the request continued its course
          filterChain.doFilter(request, response);

        } catch (Exception exception) {

          //token is not valid.
          //Not able to verify token.
          //token is expired.

          log.error("Error logging in: {}", exception.getMessage());
          response.setHeader("error ", exception.getMessage());
          response.setStatus(FORBIDDEN.value());

//          response.sendError(FORBIDDEN.value());

          Map<String, String> error = new HashMap<>();
          error.put("errorMessage", exception.getMessage());
          response.setContentType(APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), error);

        }

      } else {
        //so if the request do not have an authorization header
        //we are just going to let the request continue.
        filterChain.doFilter(request, response);
      }
    }

  }
}
