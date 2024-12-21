package com.MainBackendService.config;

import com.MainBackendService.argumentResolver.AccessTokenDetailsArgumentResolver;
import com.MainBackendService.interceptors.LoggerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AccessTokenDetailsArgumentResolver accessTokenDetailsArgumentResolver;
    @Value("${apiPrefix}")
    private String apiPrefix;
    @Value("${spring.front.end.client.url}")
    private String frontendServer;

    public WebConfig(AccessTokenDetailsArgumentResolver accessTokenDetailsArgumentResolver) {
        this.accessTokenDetailsArgumentResolver = accessTokenDetailsArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor());

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenDetailsArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")  // Allows all endpoints
                .allowedOrigins(frontendServer)  // Allow only requests from localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allowed HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true);  // Allow credentials (cookies, HTTP authentication, etc.)
    }
}
