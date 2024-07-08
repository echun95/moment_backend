package com.moment.config.security;

import com.moment.config.jwt.filter.JwtAuthFilter;
import com.moment.config.jwt.filter.JwtAuthenticationExceptionFilter;
import com.moment.config.jwt.handler.AuthEntryPointHandler;
import com.moment.config.jwt.handler.AuthenticationAccessDeniedHandler;
import com.moment.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.moment.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.moment.config.oauth2.service.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig{
    public static final String ROLE_ADMIN = "ADMIN";
    private final OAuth2MemberService oAuth2MemberService;
    private final JwtAuthFilter jwtAuthFilter;
    // OAuth2 인증 성공시, 수행하는 Handler
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // OAuth2 인증 실패시, 수행하는 Handler
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final AuthEntryPointHandler authEntryPointHandler;
    private final AuthenticationAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationExceptionFilter jwtAuthenticationExceptionFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http.cors(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/oauth2/authorization/**").permitAll()
                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/error", "/favicon.ico", "*.png", "*.gif", "*.svg", "*.jpg", "*.html", "*.css", "*.js").permitAll()
                .requestMatchers(HttpMethod.GET, "/test/**").authenticated()
                .requestMatchers("/admin/**").hasRole(ROLE_ADMIN)
                .anyRequest().authenticated()
        );

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2MemberService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationExceptionFilter, JwtAuthFilter.class);

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authEntryPointHandler));
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 모든 출처 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
