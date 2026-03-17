package dev.shop.config;

import dev.shop.authentication.JwtAuthenticationFilter;
import dev.shop.authentication.JwtTokenProvider;
import dev.shop.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO : 인코더 변경
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST API이므로 CSRF 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/login.html").permitAll()

                        // 2. GET /users/{username} 경로는 인증 필요! (순서가 중요하므로 먼저 배치)
                        // "/users/*" 는 /users/spring_fan 등 한 단계 하위 경로를 모두 잡아줌
                        .requestMatchers(HttpMethod.GET, "/users/*").authenticated()

                        // 3. 나머지 모든 GET 요청은 조건 없이 허용 (시리즈 목록, 평점 목록 등)
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()

                        // 4. 그 외의 모든 HTTP 메서드(POST, PUT, DELETE)나 지정되지 않은 경로는 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
