package com.curelex.healthcare.config;

import com.curelex.healthcare.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // =====================
                        // PUBLIC APIs
                        // =====================
                        .requestMatchers(
                                "/patient/signup",
                                "/patient/login",
                                "/doctor/signup",
                                "/doctor/login",
                                "/auth/forgot",
                                "/auth/reset"
                        ).permitAll()

                        .requestMatchers("/appointment/book")
                        .hasAuthority("ROLE_PATIENT")

                        .requestMatchers(
                                "/appointment/**/confirm",
                                "/appointment/**/complete"
                        ).hasAuthority("ROLE_DOCTOR")

                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/files/**")
                        .authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}