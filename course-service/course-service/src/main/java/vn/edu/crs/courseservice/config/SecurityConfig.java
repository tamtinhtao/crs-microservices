package vn.edu.crs.courseservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.crs.courseservice.security.JwtAuthFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // INTERNAL API
                        // registration-service gọi trực tiếp
                        // không cần JWT
                        // =========================
                        .requestMatchers("/internal/**")
                        .permitAll()

                        // =========================
                        // GET COURSE - PUBLIC
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/courses",
                                "/courses/**"
                        )
                        .permitAll()

                        // =========================
                        // CREATE - ADMIN
                        // =========================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/courses",
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // UPDATE - ADMIN
                        // =========================
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/courses",
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        // =========================
                        // DELETE - ADMIN
                        // =========================
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/courses",
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}