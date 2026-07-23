package com.ksp.shodhana.config;

import com.ksp.shodhana.security.JwtAuthenticationFilter;
import com.ksp.shodhana.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Enterprise Security Configuration & Role-Based Access Control (RBAC).
 * Configures Spring Security filter chain, JWT authentication filter, and route protection.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints for zero-dependency demo mode
                        .requestMatchers(
                                "/api/v1/ai/query",
                                "/api/v1/ai/stream",
                                "/api/v1/crimes/**",
                                "/api/v1/criminals/**",
                                "/api/v1/network/**",
                                "/api/v1/timeline/**",
                                "/api/v1/reports/**",
                                "/api/v1/settings",
                                "/error",
                                "/actuator/**"
                        ).permitAll()
                        // Restricted Vault Administration & Command endpoints requiring ROLE_SUPERINTENDENT
                        .requestMatchers("/api/v1/admin/**").hasRole("SUPERINTENDENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public boolean isOfficerAuthorized(String token, String requiredRole) {
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        String role = jwtTokenProvider.getRole(token);
        return "ROLE_SUPERINTENDENT".equals(role) || requiredRole.equals(role) || "ROLE_OFFICER".equals(role);
    }
}
