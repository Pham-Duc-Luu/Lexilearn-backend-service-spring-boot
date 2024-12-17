package com.MainBackendService.interceptors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConfigurationProperties(prefix = "server")
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "x-api-key";

    @Value("${api.key}")
    private  String VALID_API_KEY ; // Replace with your actual API key
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
            return false; // Stop further processing
        }

        return true; // Continue to the next handler
    }
}
