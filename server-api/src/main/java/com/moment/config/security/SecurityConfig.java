package com.moment.config.security;

import com.moment.config.jwt.filter.JwtAuthFilter;
import com.moment.config.jwt.handler.AuthEntryPointHandler;
import com.moment.config.jwt.handler.AuthenticationAccessDeniedHandler;
import com.moment.config.jwt.provider.JwtProvider;
import com.moment.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.moment.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.moment.config.oauth2.service.OAuth2MemberService;
import com.moment.config.security.service.MemberDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors(AbstractHttpConfigurer::disable);
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/oauth2/authorization/**").permitAll()
                .requestMatchers("/error").permitAll()
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

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authEntryPointHandler));
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
}
