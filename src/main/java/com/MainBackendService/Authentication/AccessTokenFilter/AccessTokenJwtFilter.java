package com.MainBackendService.Authentication.AccessTokenFilter;

import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.JwtClaims;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AccessTokenJwtFilter extends OncePerRequestFilter {

    private final AccessTokenJwtService accessTokenJwtService;
    Logger logger = LogManager.getLogger(AccessTokenJwtFilter.class);
    @Value("${private.key}")
    private String privateKey;

    @Autowired
    public AccessTokenJwtFilter(AccessTokenJwtService accessTokenJwtService) {
        this.accessTokenJwtService = accessTokenJwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromHeader(request);
        if (token == null) {
            // Create a custom error response
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Missing tokens",
                    request.getRequestURI()
            );

            // Set the response status to 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // Set the content type to JSON
            response.setContentType("application/json");
            // Write the error response as JSON
            try (PrintWriter writer = response.getWriter()) {
                writer.write(errorResponseToJson(errorResponse));
            }
            // Log the error message
            logger.error("Authentication failed: missing access token");
            return;
        } else {
            try {
                DecodedJWT decodedJWT = accessTokenJwtService.verify(token);
                Claim email_claim = decodedJWT.getClaim(JwtClaims.USER_EMAIL.getClaimName());
                Claim name_claim = decodedJWT.getClaim(JwtClaims.USER_NAME.getClaimName());

                logger.debug("Extracted email: {}", email_claim.asString());
                logger.debug("Extracted name: {}", name_claim.asString());


                SecurityContextHolder.getContext().setAuthentication(
                        new CustomJwtAuth(email_claim.asString(), name_claim.asString(), null)
                );

            } catch (JWTVerificationException e) {
                // Create a custom error response
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                );

                // Set the response status to 401 Unauthorized
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                // Set the content type to JSON
                response.setContentType("application/json");
                // Write the error response as JSON
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorResponseToJson(errorResponse));
                }
                // Log the error message
                logger.error("Authentication failed: {}", e.getMessage());

            } catch (Exception e) {
                // Create a custom error response
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        e.getMessage(),
                        request.getRequestURI()
                );

                // Set the response status to 401 Unauthorized
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

                // Set the content type to JSON
                response.setContentType("application/json");

                // Write the error response as JSON
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorResponseToJson(errorResponse));
                }
                // Log the error message
                logger.error("Authentication failed: {}", e.getMessage());

            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    // Convert HttpErrorDto to JSON string (you could use a library like Jackson or Gson for this)
    private String errorResponseToJson(HttpErrorDto errorResponse) {
        return "{" +
                "\"timestamp\":\"" + errorResponse.getTimestamp() + "\"," +
                "\"status\":" + errorResponse.getStatus() + "," +
                "\"error\":\"" + errorResponse.getError() + "\"," +
                "\"message\":\"" + errorResponse.getMessage() + "\"," +
                "\"path\":\"" + errorResponse.getPath() + "\"" +
                "}";
    }
}
