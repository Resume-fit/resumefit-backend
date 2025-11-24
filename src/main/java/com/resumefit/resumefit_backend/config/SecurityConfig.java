package com.resumefit.resumefit_backend.config;

import com.resumefit.resumefit_backend.auth.filter.JwtFilter;
import com.resumefit.resumefit_backend.auth.util.JWTUtil;
import com.resumefit.resumefit_backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    private static final String[] SWAGGER_PATHS = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(SWAGGER_PATHS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // Form Login 비활성화
        http.formLogin(form -> form.disable());

        // HTTP Basic 비활성화
        http.httpBasic(basic -> basic.disable());

        // 세션 완전 비활성화 (JSESSIONID 생성 방지)
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            session.sessionFixation().none();  // 세션 고정 보호 비활성화
        });

        // URL 접근 권한
        http.authorizeHttpRequests(auth -> auth
            // OPTIONS 요청은 모두 허용 (CORS preflight)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 인증 없이 접근 가능한 경로
            .requestMatchers(
                "/",
                "/index.html",
                "/api/join",
                "/api/auth/login",
                "/api/auth/logout",
                "/api/reissue",
                "/api/job-positions/**",
                "/api/admin/**"
            ).permitAll()
            // 나머지는 인증 필요
            .anyRequest().authenticated()
        );

        // JWT 필터 추가
        http.addFilterBefore(
            new JwtFilter(jwtUtil, userRepository),
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}