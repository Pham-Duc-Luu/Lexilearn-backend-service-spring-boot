package com.MainBackendService.Authentication.ApiKeyFilter;

import com.MainBackendService.dto.HttpErrorDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;


public class ApiKeyAuthFilter extends GenericFilterBean {

    private final ApiKeyAuthExtractor extractor;

    public ApiKeyAuthFilter(ApiKeyAuthExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Authentication> authenticationOptional = extractor.extract(request);
        authenticationOptional.ifPresent(SecurityContextHolder.getContext()::setAuthentication);

//        if (authenticationOptional.isEmpty()) {
//            // Create a custom error response
//            HttpErrorDto errorResponse = new HttpErrorDto(
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    "Missing or invalid api key",
//                    request.getRequestURI()
//            );
//
//            // Set the response status to 401 Unauthorized
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//            // Set the content type to JSON
//            response.setContentType("application/json");
//
//            // Write the error response as JSON
//            try (PrintWriter writer = response.getWriter()) {
//                writer.write(errorResponseToJson(errorResponse));
//            }
//
//            // Log the error message
//            logger.error("Authentication failed: Missing or invalid api key");
//
//            // Don't continue with the filter chain
//            return;
//        }

        filterChain.doFilter(request, response);
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

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }
}