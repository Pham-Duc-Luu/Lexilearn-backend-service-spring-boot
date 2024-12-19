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
public class AccessTokenJwtService {
    Logger logger = LogManager.getLogger(AccessTokenJwtService.class);
    // * set the algorithm for the jwt token
    Algorithm algorithm;
    @Value("${public.key}")
    private String publicKey;
    @Value("${public.time}")
    private String publicTime;

    @PostConstruct
    public void initAlgorithm() {
        this.algorithm = Algorithm.HMAC256(publicKey);
    }

    public String genToken(String user_name, String user_email) {

        String accessToken = JWT.create()
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), user_email)
                .withClaim(JwtClaims.USER_NAME.getClaimName(), user_name)
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(publicTime))))
                .sign(algorithm);

        return accessToken;
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
