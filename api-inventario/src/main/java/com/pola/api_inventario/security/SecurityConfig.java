package com.pola.api_inventario.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                /*
                                 * .cors(cors -> cors.configurationSource(request -> {
                                 * CorsConfiguration config = new CorsConfiguration();
                                 * config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                                 * config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
                                 * "OPTIONS"));
                                 * config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type",
                                 * "x-requested-with"));
                                 * config.setExposedHeaders(Arrays.asList("Authorization"));
                                 * config.setAllowCredentials(true);
                                 * return config;
                                 * }))
                                 */
                                .authorizeHttpRequests(authRequest -> authRequest
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/ws/public/**").permitAll()
                                                .requestMatchers("/ws/**").permitAll()
                                                .requestMatchers("/mensajes").permitAll()
                                                .requestMatchers("/api/v1/items/**").hasRole("PROVEEDOR")
                                                .anyRequest().authenticated())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }
}
