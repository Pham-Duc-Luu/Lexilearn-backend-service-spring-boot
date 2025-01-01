package com.MainBackendService.filter;

import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.UserService;
import com.jooq.sample.model.tables.records.UserRecord;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Order(3)
//@Component
public class DeskOwnershipFilter implements Filter {
    private final DeskService deskService;
    private final UserService userService;
    Logger logger = LogManager.getLogger(DeskOwnershipFilter.class);

    @Autowired
    public DeskOwnershipFilter(DeskService deskService, UserService userService) {
        this.deskService = deskService;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        // Check if the request is targeting a desk operation (e.g., /api/v1/desk/{desk_id}/...)
        String deskId = extractDeskIdFromUri(requestUri);
        logger.debug(deskId);
        if (deskId != null) {
            // Retrieve the user details
            String email = (String) request.getAttribute("email");

            // Example of user identification
            if (email == null) {
                throw new SecurityException("User not authenticated.");
            }
            Optional<UserRecord> existUser = userService.findUserByEmail(email);
            if (existUser.isEmpty()) {
                throw new SecurityException("Some thing went wrong with user info.");
            }
            // Validate ownership
            boolean isOwner = deskService.isUserOwnerOfDesk(existUser.get().getUserId(), Integer.valueOf((deskId)));
            if (!isOwner) {
                // Create a custom error response
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "You are not allow to modify this resource",
                        httpRequest.getRequestURI()
                );

                // Set the response status to 401 Unauthorized
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                // Set the content type to JSON
                response.setContentType("application/json");
                // Write the error response as JSON
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(errorResponseToJson(errorResponse));
                }
                return;
            }
        }

        // Continue processing
        chain.doFilter(request, response);
    }

    private String errorResponseToJson(HttpErrorDto errorResponse) {
        return "{" +
                "\"timestamp\":\"" + errorResponse.getTimestamp() + "\"," +
                "\"status\":" + errorResponse.getStatus() + "," +
                "\"error\":\"" + errorResponse.getError() + "\"," +
                "\"message\":\"" + errorResponse.getMessage() + "\"," +
                "\"path\":\"" + errorResponse.getPath() + "\"" +
                "}";
    }

    private String extractDeskIdFromUri(String uri) {
        // Regex to extract desk_id
        List<Pattern> ListPattern = List.of(
                Pattern.compile("/api/v1/desk/(\\d+)/.*"),
                Pattern.compile("/api/v1/desk/(\\d+)")

        );
        for (Pattern pattern : ListPattern) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) return matcher.group(1);
        }
        return null;
    }
}