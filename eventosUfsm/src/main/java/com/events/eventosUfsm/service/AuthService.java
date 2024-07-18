package com.events.eventosUfsm.service;

import com.events.eventosUfsm.middleware.auth.JwtService;
import com.events.eventosUfsm.model.user.Role;
import com.events.eventosUfsm.model.user.User;
import com.events.eventosUfsm.repository.UserRepository;
import com.events.eventosUfsm.routes.AuthRoute;
import com.events.eventosUfsm.shared.Output;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  @Value("${app.image-directory}")
  private String imageDirectory;

  public String saveImage(MultipartFile imgUser) throws IOException {
    Path directoryPath = Paths.get(imageDirectory);
    if (!Files.exists(directoryPath)) {
      Files.createDirectories(directoryPath);
    }
    String fileName = imgUser.getOriginalFilename();
    Path filePath = directoryPath.resolve(fileName);
    Files.copy(imgUser.getInputStream(), filePath);
    return filePath.toString();
  }

  public ResponseEntity<?> register(AuthRoute.RegisterDTO request) {

    int size = request.toString().length();
    Output.info(request.toString().substring(0, size - 20) + "...");
    User found = userRepository.findByEmail(request.email());

    if (found != null) {
      final String error = "Provided email is already in use.";
      final String message = "Please, navigate to the login section.";
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthRoute.ErrorDTO(error, message));
    }

    var user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(hashPassword(request.password()))
            .role(Role.USER)
            .build();

    if (user != null)
      userRepository.save(user);

    var accessToken = jwtService.generateAccessToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);


    return ResponseEntity.status(HttpStatus.OK).body(
            new AuthRoute.TokensResponse(accessToken, refreshToken));
  }
  public ResponseEntity<?> edit(String authHeader, AuthRoute.EditDTO request) {
    User currentUser = getUserFromToken(authHeader);

    if (currentUser == null) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    User userByEmail = userRepository.findByEmail(request.email());
    if (userByEmail != null && !userByEmail.getUserId().equals(currentUser.getUserId())) {
      return ResponseEntity.status(409).body("Email already in use.");
    }

    currentUser.setFirstName(request.firstName());
    currentUser.setLastName(request.lastName());
    currentUser.setEmail(request.email());

    userRepository.save(currentUser);

    return ResponseEntity.ok(currentUser);
  }

  private User getUserFromToken(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    String token = authHeader.substring(7);
    try {
      String email = jwtService.extractUsername(token);
      return userRepository.findByEmail(email);
    } catch (Exception e) {
      System.out.println("Token parsing failed: " + e.getMessage());
      return null;
    }
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

  public ResponseEntity<?> privilege(AuthRoute.RegisterDTO request) {

    var user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(hashPassword(request.password()))
            .role(Role.ADMIN)
            .build();

    if (user != null) userRepository.save(user);

    var accessToken = jwtService.generateAccessToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    Output.done("Access-Token and Refresh-Token Generated");

    return ResponseEntity.status(HttpStatus.OK).body(
            new AuthRoute.TokensResponse(accessToken, refreshToken));
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

  private User createUser(AuthRoute.RegisterDTO request, Role role, MultipartFile imgUser) throws IOException {
    String imgUserUrl = saveImage(imgUser);
    return User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .imgUser(imgUserUrl)
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
