package no.ntnu.userservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author "https://github.com/iHateThisName"
 * @version 1.0
 */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  //used to authenticate the user.
  private final AuthenticationManager authenticationManager;

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }


  /**
   * user try to authenticate
   * This methode is going to be called because we are going to try to attempt to authenticate.
   * if the authentication is not successful, spring is just going to spit out an error to the user
   * But if the authentication is successful, then it's going to call the successfulAuthentication methode.
   *
   * @throws AuthenticationException throws an error when the authentication was not successful.
   */
  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    log.info("Username is: {}", username);
    log.info("Password is: {}", password);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(authenticationToken);
  }

  /**
   * This methode will send the access token and the refresh token.
   * Whenever the login is successful.
   * The methode is called when the login is successful.
   *
   * @param request
   * @param response
   * @param chain
   * @param authentication
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authentication) throws IOException, ServletException {

    //here we are going to generate the token

    //This user is from org.springframework.security.core.userdetails.User
    User user = (User) authentication.getPrincipal();
    //todo "secret" is a password that is not going to production
    // need to creat a utility class that can decrypt and pass it here.
    //This will sign the web token
    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

    String accessToken = JWT.create()
        .withSubject(user.getUsername())
        //expire date for the access token.
        .withExpiresAt(java.sql.Date.valueOf(LocalDate.now().plusWeeks(1)))
        //the issuer is the name of the author of this token
        .withIssuer(request.getRequestURL().toString())
         //
        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .sign(algorithm);

    String refreshToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
        .withIssuer(request.getRequestURL().toString())
        .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .sign(algorithm);

    response.setHeader("accessToken", accessToken);
    response.setHeader("refreshToken", refreshToken);


  }
}
