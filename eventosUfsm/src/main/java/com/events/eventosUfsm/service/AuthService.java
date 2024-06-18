package com.events.eventosUfsm.service;

import com.events.eventosUfsm.middleware.auth.JwtService;
import com.events.eventosUfsm.model.user.Role;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.UserRepository;
import com.events.eventosUfsm.routes.AuthRoute;
import com.events.eventosUfsm.shared.Output;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public ResponseEntity<?> register(@Valid AuthRoute.RegisterDTO request) {
    logRequestInfo(request);

    User foundUser = userRepository.findByEmail(request.email());
    if (foundUser != null) {
      return buildErrorResponse("Provided email is already in use.", "Please, navigate to the login section.");
    }

    User newUser = createUser(request, Role.USER);
    userRepository.save(newUser);

    return generateTokenResponse(newUser);
  }

  public ResponseEntity<?> login(AuthRoute.LoginDTO request) {
    try {
      authenticateUser(request.email(), request.password());

      User user = userRepository.findByEmail(request.email());
      return generateSignInResponse(user);
    } catch (LockedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("User account is locked. Please contact support.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Invalid credentials. Please try again.");
    }
  }

  public ResponseEntity<?> privilege(@Valid AuthRoute.RegisterDTO request) {
    User adminUser = createUser(request, Role.ADMIN);
    userRepository.save(adminUser);

    return generateTokenResponse(adminUser);
  }

  public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().build();
    }

    String refreshToken = authHeader.substring(7);
    String userEmail = jwtService.extractUsername(refreshToken);

    User userDetails = userRepository.findByEmail(userEmail);
    if (userDetails != null && jwtService.isTokenValid(refreshToken, userDetails)) {
      String accessToken = jwtService.generateAccessToken(userDetails);
      return ResponseEntity.ok(new AuthRoute.TokensResponse(accessToken, refreshToken));
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  private void logRequestInfo(AuthRoute.RegisterDTO request) {
    String requestInfo = request.toString();
    int size = requestInfo.length();
    Output.info(requestInfo.substring(0, Math.min(size, size - 20)) + "...");
  }

  private ResponseEntity<AuthRoute.ErrorDTO> buildErrorResponse(String error, String message) {
    AuthRoute.ErrorDTO errorDTO = new AuthRoute.ErrorDTO(error, message);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
  }

  private User createUser(AuthRoute.RegisterDTO request, Role role) {
    return User.builder()
      .firstName(request.firstName())
      .lastName(request.lastName())
      .email(request.email())
      .password(hashPassword(request.password()))
      .role(role)
      .build();
  }

  private String hashPassword(String password) {
    return passwordEncoder.encode(password);
  }

  private void authenticateUser(String email, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
  }

  private ResponseEntity<AuthRoute.TokensResponse> generateTokenResponse(User user) {
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    Output.done("Access and refresh tokens generated");

    return ResponseEntity.status(HttpStatus.OK).body(new AuthRoute.TokensResponse(accessToken, refreshToken));
  }

  private ResponseEntity<AuthRoute.SignInResponse> generateSignInResponse(User user) {
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    AuthRoute.TokensResponse tokensResponse = new AuthRoute.TokensResponse(accessToken, refreshToken);
    AuthRoute.SignInResponse signInResponse = new AuthRoute.SignInResponse(user, tokensResponse);

    System.out.println(signInResponse);

    return ResponseEntity.status(HttpStatus.OK).body(signInResponse);
  }
}
