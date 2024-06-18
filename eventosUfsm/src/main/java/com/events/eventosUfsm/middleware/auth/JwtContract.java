package com.events.eventosUfsm.middleware.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Map;
import java.util.function.Function;

public abstract class JwtContract {

  public abstract String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  public abstract String generateAccessToken(UserDetails userDetails);

  public abstract String generateRefreshToken(UserDetails userDetails);

  public abstract boolean isTokenValid(String token, UserDetails userDetails);

  public abstract boolean isTokenExpired(String token);

  public abstract String extractUsername(String token);

  public abstract <T> T extractSpecificClaim(String token, Function<Claims, T> claimsResolver);

  public abstract Claims extractAllClaims(String token);

  public abstract Key getSignInKey();
}
