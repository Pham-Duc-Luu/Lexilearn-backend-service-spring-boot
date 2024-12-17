package com.MainBackendService.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {

    // Secret key to sign the JWT (it should be stored securely, not hardcoded)

    // Method to create a JWT token
    public static final String createToken(String source, String secret ,Integer expireIimeInHour) {
        return Jwts.builder()
                .setSubject(source)  // Set the subject (user info)
                .setIssuedAt(new Date())  // Set the issued date
                .setExpiration(new Date(System.currentTimeMillis() + expireIimeInHour))  // Set expiration (e.g., 1 hour)
                .signWith(SignatureAlgorithm.HS256, secret)  // Sign the token with HS256 algorithm
                .compact();  // Compact the token to a string
    }

    // Method to parse and validate the JWT token
    public final Claims parseToken(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)  // Provide the same key used for signing
                .build()
                .parseClaimsJws(token)  // Parse the JWT token
                .getBody();  // Extract the claims from the token
    }
}
