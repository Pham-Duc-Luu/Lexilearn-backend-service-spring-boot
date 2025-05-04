package com.MainBackendService.service;

import com.MainBackendService.dto.AuthenticationDto.UserJWTObject;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.utils.KeyLoader;
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
public class AccessTokenJwtService {
    Logger logger = LogManager.getLogger(AccessTokenJwtService.class);
    // * set the algorithm for the jwt token
    private Algorithm algorithm;


    @Value("${user.jwt.access-token.private-key.path}")
    private String privateKeyPath;

    @Value("${user.jwt.access-token.public-key.path}")
    private String publicKeyPath;


    @Value("${user.jwt.access-token.duration.in.hour}")
    private String tokenDurationInHour;

    @Value("${user.jwt.access-token.algorithm}")
    private String jwtAlgorithm;

    @PostConstruct
    public void initAlgorithm() throws Exception {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Deprecated
    public String genToken(String user_name, String user_email) {

        String accessToken = JWT.create()
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), user_email)
                .withClaim(JwtClaims.USER_NAME.getClaimName(), user_name)
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(tokenDurationInHour))))
                .sign(algorithm);

        return accessToken;
    }

    public String genToken(UserJWTObject userJWTObject) throws HttpResponseException {
        return JWT.create()
                .withClaim(JwtClaims.User.getClaimName(), userJWTObject.toJson())
                .withClaim(JwtClaims.USER_EMAIL.getClaimName(), userJWTObject.getUser_email())
                .withClaim(JwtClaims.USER_NAME.getClaimName(), userJWTObject.getUser_email())
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(Duration.ofHours(Long.parseLong(tokenDurationInHour))))
                .sign(algorithm);
    }


    public DecodedJWT verify(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            return decodedJWT;
        } catch (JWTVerificationException e) {
            throw e;
        }
    }
}
