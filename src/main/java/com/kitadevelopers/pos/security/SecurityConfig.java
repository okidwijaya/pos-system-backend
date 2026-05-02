package com.kitadevelopers.pos.security;

import com.kitadevelopers.pos.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write("""
                                {"error":"Unauthorized","message":"Please login first"}
                                """);
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write("""
                                {"error":"Forbidden","message":"You don’t have access"}
                                """);
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
//role based access (hardcore based on role)
//REMOVE BC NOW using RBAC Standart (@EnableMethodSecurity) (RBAC + Permission-based access)
//                .csrf(csrf -> csrf.disable())
//        .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/products/**").hasAnyRole("ADMIN", "CASHIER")
//                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "CASHIER")
//                        .anyRequest().authenticated()
//                )

