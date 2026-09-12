package com.events.eventosUfsm.middleware.security;

import com.events.eventosUfsm.middleware.auth.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthFilter authFilter;
  private final AuthenticationProvider authenticationProvider;

  // Allowlist de origens via env var (separadas por vírgula); default cobre o dev do Ionic/Angular.
  @Value("${cors.allowed-origins:http://localhost:8100,http://localhost:4200,capacitor://localhost,http://localhost}")
  private String allowedOrigins;

  @Bean
  SecurityFilterChain filter(HttpSecurity http) throws Exception {
    return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                    // Autenticação pública
                    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/refresh-token").permitAll()

                    // Navegação pública de eventos, locais e imagens (somente leitura)
                    .requestMatchers(HttpMethod.GET, "/events/date").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events/bookmarks").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events/search").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events/type").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events/check-event").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/local").permitAll()
                    .requestMatchers(HttpMethod.GET, "/local/{id}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/local/events/{eventId}").permitAll()
                    .requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                    // Health check (Cloud Run / monitoramento) — não expõe dados de negócio
                    .requestMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()

                    // Operações administrativas
                    .requestMatchers(HttpMethod.POST, "/events/save").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/events/edit").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/events/delete").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/events/associate-locals").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/local/save").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/local/edit").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/local/delete").hasAuthority("ADMIN")

                    // Todo o resto (dados de usuário: bookmarks, comentários, avaliações,
                    // lembretes, interações, preferências) exige autenticação.
                    // O userId deve ser derivado do JWT no service, nunca do path/body.
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

}
