package com.events.eventosUfsm.routes;

import com.events.eventosUfsm.service.AuthService;
import com.events.eventosUfsm.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthRoute {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO request) {
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
}
