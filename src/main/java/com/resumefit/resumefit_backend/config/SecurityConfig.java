package com.resumefit.resumefit_backend.config;

import com.resumefit.resumefit_backend.auth.filter.JwtFilter;
import com.resumefit.resumefit_backend.auth.util.JWTUtil;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    private static final String[] SWAGGER_PATHS = {
        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api/auth/login"
    };

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(SWAGGER_PATHS);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf((auth) -> auth.disable());
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        http.authorizeHttpRequests(
                (auth) ->
                        auth
                                .requestMatchers(
                                        "/",
                                        "/index.html",
                                        "/api/join",
                                        "/api/auth/login",
                                        "/api/reissue",
                                        "/api/job-positions/**",
                                        "/api/admin/**")
                                .permitAll()
                                // .requestMatchers("/api/join", "/api/auth/login",
                                // "/api/reissue").permitAll()
                                // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .anyRequest()
                                .authenticated());

        http.addFilterBefore(
                new JwtFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
