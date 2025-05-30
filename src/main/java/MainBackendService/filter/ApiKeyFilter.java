package MainBackendService.filter;

import MainBackendService.dto.HttpErrorDto;
import MainBackendService.utils.HttpHeaderUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

@Order(1)
@Component
public class ApiKeyFilter implements Filter {
    // List of URL patterns to protect
    private final List<Pattern> protectedUrlPatterns = List.of(
            Pattern.compile("/api/v1/.*") // Protect all URLs under /api/v1/protected
    );

    @Autowired
    HttpHeaderUtil httpHeaderUtil;
    Logger logger = LogManager.getLogger(ApiKeyFilter.class);
    // Your predefined API key (store this securely in a real application)
    @Value("${api.key}")
    private String VALID_API_KEY;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.debug(VALID_API_KEY);
        // Extract API key from the "x-api-key" header
        String apiKey = httpRequest.getHeader("x-api-key");

        // Check if the requested URL matches any protected patterns
        boolean isProtected = protectedUrlPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(httpRequest.getRequestURI()).matches());

        if (isProtected) {
            if (apiKey == null) {
                // Create a custom error response
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Missing api key",
                        httpRequest.getRequestURI()
                );

                // Set the response status to 401 Unauthorized
                ((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
                // Set the content type to JSON
                response.setContentType("application/json");
                // Write the error response as JSON
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(httpHeaderUtil.errorResponseToJson(errorResponse));
                }
                // Log the error message
                logger.error("Authentication failed: missing access token");
                return;
            }

            // Validate the API key
            if (!VALID_API_KEY.equals(apiKey)) {
                // Create a custom error response
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "Invalid api key",
                        httpRequest.getRequestURI()
                );

                // Set the response status to 401 Unauthorized
                ((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
                // Set the content type to JSON
                response.setContentType("application/json");
                // Write the error response as JSON
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(httpHeaderUtil.errorResponseToJson(errorResponse));
                }
                // Log the error message
                logger.error("Authentication failed: missing access token");
                return; // Stop further processing
            }
        }
        // Proceed to the next filter in the chain
        chain.doFilter(request, response);
    }


}
