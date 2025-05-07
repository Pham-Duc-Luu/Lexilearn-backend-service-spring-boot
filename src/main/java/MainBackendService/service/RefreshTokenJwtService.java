package MainBackendService.service;

import MainBackendService.dto.AuthenticationDto.UserJWTObject;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.utils.KeyLoader;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;

@Service
public class RefreshTokenJwtService {
    Logger logger = LogManager.getLogger(RefreshTokenJwtService.class);
    // * set the algorithm for the jwt token
    Algorithm algorithm;
    @Value("${user.jwt.refresh-token.private-key.path}")
    private String privateKeyPath;

    @Value("${user.jwt.refresh-token.public-key.path}")
    private String publicKeyPath;


    @Value("${user.jwt.refresh-token.duration.in.hour}")
    private String tokenDurationInHour;

    @Value("${user.jwt.refresh-token.algorithm}")
    private String jwtAlgorithm;

    @PostConstruct
    public void initAlgorithm() throws Exception {

        // Load RSA keys from the resources folder
        RSAPrivateKey privateKey = KeyLoader.loadPrivateKey(privateKeyPath);
        RSAPublicKey publicKey = KeyLoader.loadPublicKey(publicKeyPath);

        switch (jwtAlgorithm) {
            case "RSA256":
                this.algorithm = Algorithm.RSA256(publicKey, privateKey);
            case "RSA384":
                this.algorithm = Algorithm.RSA384(publicKey, privateKey);
            case "RSA512":
                this.algorithm = Algorithm.RSA512(publicKey, privateKey);
            default:
                this.algorithm = Algorithm.RSA256(publicKey, privateKey);

        }
    }

    @Deprecated
    public String genToken(String user_name, String user_email) {

        String refreshToken = JWT.create()
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), user_email)
                .withClaim(JwtClaims.USER_NAME.getClaimName(), user_name)
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(tokenDurationInHour))))
                .sign(algorithm);
        return refreshToken;
    }

    public String genToken(UserJWTObject userJWTObject) throws HttpResponseException {
        String refreshToken = JWT.create()
                .withClaim(JwtClaims.User.getClaimName(), userJWTObject.toJson())
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), userJWTObject.getUser_email())
                .withClaim(JwtClaims.USER_NAME.getClaimName(), userJWTObject.getUser_email())
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(tokenDurationInHour))))
                .sign(algorithm);

        return refreshToken;
    }


    public DecodedJWT verify(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            return decodedJWT;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }
}
