package MainBackendService.utils;

import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.HttpErrorDto;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.exception.HttpUnauthorizedException;
import MainBackendService.service.AccessTokenJwtService;
import MainBackendService.service.JwtClaims;
import MainBackendService.service.UserService.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jooq.sample.model.tables.records.UserRecord;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HttpHeaderUtil {
    private final UserService userService;
    Logger logger = LogManager.getLogger(HttpHeaderUtil.class);
    @Autowired
    AccessTokenJwtService accessTokenJwtService;

    @Autowired
    public HttpHeaderUtil(UserService userService) {
        this.userService = userService;
    }

    public AccessTokenDetailsDto accessTokenVerification(HttpServletRequest request) throws HttpResponseException {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            throw new HttpUnauthorizedException();
        }

        // Extract values from request attributes
        String email = (String) request.getAttribute("email");
        String name = (String) request.getAttribute("name");
        Optional<UserRecord> existUser = userService.findUserByEmail(email);
        if (existUser.isEmpty()) throw new HttpNotFoundException("Some thing went wrong");

        if (email == null || name == null) {
            throw new HttpUnauthorizedException("Unable to verify your token, please try to sign in again");
        }

        return new AccessTokenDetailsDto(existUser.get().getUserId(), email, name);
    }

    public AccessTokenDetailsDto accessTokenVerification(String token) throws HttpResponseException {
        if (token == null) {
            throw new HttpUnauthorizedException();
        }

        DecodedJWT decodedJWT = accessTokenJwtService.verify(token);
        String email = decodedJWT.getClaim(JwtClaims.USER_EMAIL.getClaimName()).asString();
        String name = decodedJWT.getClaim(JwtClaims.USER_NAME.getClaimName()).asString();

        Optional<UserRecord> existUser = userService.findUserByEmail(email);
        if (existUser.isEmpty()) throw new HttpNotFoundException("Some thing went wrong");

        if (email == null || name == null) {
            throw new HttpUnauthorizedException("Unable to verify your token, please try to sign in again");
        }

        return new AccessTokenDetailsDto(existUser.get().getUserId(), email, name);
    }

    public AccessTokenDetailsDto accessTokenVerification(List<String> tokens) throws HttpResponseException {
        try {

            String token = extractBearerFromAuthenticationList(tokens);
            if (token == null) {
                throw new HttpUnauthorizedException();
            }

            DecodedJWT decodedJWT = accessTokenJwtService.verify(token);
            String email = decodedJWT.getClaim(JwtClaims.USER_EMAIL.getClaimName()).asString();
            String name = decodedJWT.getClaim(JwtClaims.USER_NAME.getClaimName()).asString();

            Optional<UserRecord> existUser = userService.findUserByEmail(email);
            if (existUser.isEmpty()) throw new HttpNotFoundException("Some thing went wrong");

            if (email == null || name == null) {
                throw new HttpUnauthorizedException("Unable to verify your token, please try to sign in again");
            }


            return new AccessTokenDetailsDto(existUser.get().getUserId(), email, name);
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw new HttpUnauthorizedException(e.getMessage());
        }
    }


    public String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    public String extractBearerFromAuthenticationList(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }

        for (String token : tokens) {
            if (token != null && token.startsWith("Bearer ")) {
                return token.substring(7); // Remove "Bearer " prefix
            }
        }
        return null;
    }

    // Convert HttpErrorDto to JSON string (you could use a library like Jackson or Gson for this)
    public String errorResponseToJson(HttpErrorDto errorResponse) {
        return "{" +
                "\"timestamp\":\"" + errorResponse.getTimestamp() + "\"," +
                "\"status\":" + errorResponse.getStatus() + "," +
                "\"error\":\"" + errorResponse.getError() + "\"," +
                "\"message\":\"" + errorResponse.getMessage() + "\"," +
                "\"path\":\"" + errorResponse.getPath() + "\"" +
                "}";
    }
}
