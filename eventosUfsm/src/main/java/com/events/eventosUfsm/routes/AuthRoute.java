package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.middleware.auth.JwtService;
import com.events.eventosUfsm.repository.UserRepository;
import com.events.eventosUfsm.service.AuthService;
import com.events.eventosUfsm.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthRoute {

  private final AuthService authService;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @PostMapping("/register")
  public ResponseEntity<?>register(@Valid @RequestBody RegisterDTO request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDTO request) {
    return authService.login(request);
  }

  @PostMapping("/privilege")
  public ResponseEntity<?> privilege(@RequestBody RegisterDTO request) {
    return authService.privilege(request);
  }


  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    return authService.refreshToken(authHeader);
  }
  @PutMapping("/edit")
  public ResponseEntity<?> edit(@Valid @RequestBody EditDTO request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    return authService.edit(authHeader, request);
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
  public static record LoginDTO(
    String email,
    String password
  ) { }

  public static record SignInResponse(
    User user,
    TokensResponse tokens
  ) {}

  public static record ErrorDTO(
    String error,
    String message
  ) { }

  public static record TokensResponse(
    String accessToken,
    String refreshToken
  ) { }

  public static record RegisterDTO(

    String firstName,
    String lastName,
    String email,
    String password
  ) { }
  public static record EditDTO(
          String firstName,
          String lastName,
          String email
  ) { }
}
