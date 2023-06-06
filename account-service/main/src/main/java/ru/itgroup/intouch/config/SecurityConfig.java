package ru.itgroup.intouch.config;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.itgroup.intouch.config.jwt.JWTRequestFilter;
import ru.itgroup.intouch.service.security.UserDetailsServiceImpl;


@Configuration
@EnableWebMvc
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {


    private final JWTRequestFilter jwtRequestFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsServiceImpl userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                )
                .and()
                .authorizeHttpRequests(requests -> {
                    try {
                        requests
                                .requestMatchers("/api/v1/account/me").hasRole("USER")
                                .requestMatchers("/api/v1/auth/logout").hasRole("USER")
                                .requestMatchers("/**").permitAll();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                });
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
