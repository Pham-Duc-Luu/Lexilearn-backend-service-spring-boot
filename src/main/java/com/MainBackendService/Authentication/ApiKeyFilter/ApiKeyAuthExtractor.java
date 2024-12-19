package com.MainBackendService.Authentication.ApiKeyFilter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApiKeyAuthExtractor {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    
    @Value("${application.security.api-key}")
    private String apiKey;

    public Optional<Authentication> extract(HttpServletRequest request) {
        String providedKey = request.getHeader("ApiKey");
        if (providedKey == null || !providedKey.equals(apiKey))
            return Optional.empty();

        return Optional.of(new ApiKeyAuth(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }

}