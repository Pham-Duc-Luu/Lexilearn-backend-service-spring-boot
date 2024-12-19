package com.MainBackendService.config;

import com.MainBackendService.Authentication.AccessTokenFilter.AccessTokenJwtFilter;
import com.MainBackendService.Authentication.ApiKeyFilter.ApiKeyAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity // Enables method-level security (e.g., @PreAuthorize)
public class SecurityFilterConfig {
    private final AccessTokenJwtFilter accessTokenJwtFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;
    @Value("${apiPrefix}")
    private String apiPrefix;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;
    @Value("${java.env}")
    private String javaEnv;

    public SecurityFilterConfig(AccessTokenJwtFilter accessTokenJwtFilter, ApiKeyAuthFilter apiKeyAuthFilter) {
        this.accessTokenJwtFilter = accessTokenJwtFilter;

        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    @Order(1)
    SecurityFilterChain accessTokenFilterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println(apiPrefix);
        return httpSecurity
                .securityMatcher(AntPathRequestMatcher.antMatcher("/api/**"))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).permitAll()

//                        .requestMatchers(AntPathRequestMatcher.antMatcher(apiPrefix + "/**")).authenticated()
                ).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/auth/**"))
                .httpBasic(Customizer.withDefaults()).build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//
//        return http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .formLogin(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(registry -> registry
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/sign-in")).permitAll()
//                        .requestMatchers(apiPrefix + "/**").authenticated()
//
//                )
//                .addFilterAfter(accessTokenJwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .authorizeHttpRequests(registry -> registry
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
//                        .anyRequest().authenticated()
//                ).addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
//
//                .build();
//    }
}
