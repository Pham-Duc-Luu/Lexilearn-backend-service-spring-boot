package com.MainBackendService.service;

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

import java.time.Duration;
import java.time.Instant;

@Service
public class RefreshTokenJwtService {
    Logger logger = LogManager.getLogger(RefreshTokenJwtService.class);
    // * set the algorithm for the jwt token
    Algorithm algorithm;
    @Value("${private.key}")
    private String privateKey;
    @Value("${private.time}")
    private String privateTime;

    @PostConstruct
    public void initAlgorithm() {
        this.algorithm = Algorithm.HMAC256(privateKey);
    }

    public String genToken(String user_name, String user_email) {

        String refreshToken = JWT.create()
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), user_email)
                .withClaim(JwtClaims.USER_NAME.getClaimName(), user_name)
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(privateTime))))
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
