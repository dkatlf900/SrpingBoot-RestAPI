package com.example.szssonjunyoung.config;


import com.example.szssonjunyoung.base.dto.ApiResult;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.token.JwtTokenProvider;
import com.example.szssonjunyoung.core.token.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;



@Configuration
@EnableWebSecurity(debug = false)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**","/swagger-ui/html/**"
                                , "/configuration/**","/swagger-resources/**", "/swagger-ui/index.html/**"
                                , "/webjars/**","/v3/api-docs/**").permitAll()
                        .requestMatchers("/szs/signup").permitAll() // 회원가입 허용
                        .requestMatchers("/szs/login").permitAll() // 로그인 허용
                        .requestMatchers("/szs/**").hasRole("USER") // 그외 jwt 검증
                        .anyRequest().authenticated()
                )
                .csrf((csrf) -> csrf.disable())
                .httpBasic(withDefaults())
                .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable())) // h2 console 보려면 필요
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write(new ObjectMapper().writeValueAsString(
                                    new ApiResult<>(ErrorCode.ERROR_401.getCode(), "Unauthorized (Token missing or invalid, please provide a valid token.)")
                            ));
                        })
                )
            ;
        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}