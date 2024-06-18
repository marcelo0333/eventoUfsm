package com.events.eventosUfsm.middleware.security;

import com.events.eventosUfsm.middleware.auth.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthFilter authFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  SecurityFilterChain filter(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(authz -> authz
        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/bookmarks/save").permitAll()
        .requestMatchers(HttpMethod.POST, "/comments/save").permitAll()
        .requestMatchers(HttpMethod.GET, "/events/date").permitAll()
        .requestMatchers(HttpMethod.GET, "/events").permitAll()
        .requestMatchers(HttpMethod.GET, "/events/{id}").permitAll()
        .requestMatchers(HttpMethod.POST, "/events/save").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/events/edit").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/events/delete").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.POST, "/events/associate-locals").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.GET, "/local").permitAll()
        .requestMatchers(HttpMethod.GET, "/local/{id}").permitAll()
        .requestMatchers(HttpMethod.GET, "/local/events/{eventId}").permitAll()
        .requestMatchers(HttpMethod.POST, "/local/save").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/local/edit").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/local/delete").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.POST, "/rating/save").permitAll()
        .requestMatchers(HttpMethod.PUT, "/rating/edit").permitAll()
        .anyRequest().authenticated()
      )
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

}
