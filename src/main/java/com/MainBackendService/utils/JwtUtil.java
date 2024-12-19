package com.MainBackendService.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JwtUtil {
    Logger logger = LogManager.getLogger(JwtUtil.class);

    // Secret key to sign the JWT (it should be stored securely, not hardcoded)

    // Method to create a JWT token
    public String createToken(String source, String secret, int expireTimeInHour) {

        Date oldDate = new Date(); // oldDate == current time

        Date newDate = new Date(oldDate.getTime() + TimeUnit.HOURS.toMillis(Integer.valueOf(expireTimeInHour))); // Add 2 hours

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String jwt = JWT.create().withIssuer("DecodedJWT")
                .withSubject("DecodedJWT Details")
                .withClaim("userId", "1234")
                .withIssuedAt(new Date())
                .withExpiresAt(newDate)
                .withJWTId(UUID.randomUUID()
                        .toString())
                .sign(algorithm);
        return jwt;
    }

    // Method to parse and validate the JWT token
//    public final Claims parseToken(String token, String secret) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secret)  // Provide the same key used for signing
//                .build()
//                .parseClaimsJws(token)  // Parse the JWT token
//                .getBody();  // Extract the claims from the token
//    }


//    public boolean isTokenExpired(Claims claims) {
//        return claims.getExpiration().before(new Date());
//    }
}
