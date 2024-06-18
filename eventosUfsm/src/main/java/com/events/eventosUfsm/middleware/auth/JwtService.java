package com.events.eventosUfsm.middleware.auth;

import com.events.eventosUfsm.shared.Output;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService extends JwtContract {

  private static final long SET_01_MIN = 60000L;

  private static final String SECRET_KEY = "DFatenFSYbaa+PaCOygVv8JtOc3d1UPv2BCIIeQ2TwGTA2EuhNQpGhszoUEN2bFR";

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return generateToken(extraClaims, userDetails, SET_01_MIN / 2);
  }

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, SET_01_MIN / 2);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, SET_01_MIN * 10);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
    try {
      return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuer("localhost:9090")
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
    } catch (SignatureException | ExpiredJwtException e) {
      Output.fail("Token generation failed: " + e.toString());
      return null;
    }
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean isTokenExpired(String token) {
    return extractSpecificClaim(token, Claims::getExpiration).before(new Date());
  }

  public String extractUsername(String token) {
    return extractSpecificClaim(token, Claims::getSubject);
  }

  public Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (ExpiredJwtException e) {
      Output.fail("Your session has expired. Please log in again.");
      return null;
    } catch (Exception e) {
      Output.fail("Token parsing failed: " + e.toString());
      return null;
    }
  }

  public <T> T extractSpecificClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claims != null ? claimsResolver.apply(claims) : null;
  }

  public Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
