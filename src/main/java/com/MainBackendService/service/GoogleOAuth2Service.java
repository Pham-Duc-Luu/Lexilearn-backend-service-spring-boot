package com.MainBackendService.service;

import com.MainBackendService.controller.Auth.Auth;
import com.MainBackendService.dto.GoogleUserInfoPayload;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleOAuth2Service {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String REDIRECT_URI = "postmessage"; // Match your frontend's redirect URI
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    Logger logger = LogManager.getLogger(Auth.class);
    @Value("${spring.google.client.id}")
    private String CLIENT_ID;
    @Value("${spring.google.client.secret}")
    private String CLIENT_SECRET;

    public void verifyIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JSON_FACTORY)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        try {
            logger.debug(CLIENT_ID);

            logger.debug(idTokenString);
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user information
                String userId = payload.getSubject();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");

                System.out.printf("User ID: %s%nEmail: %s%nName: %s%n", userId, email, name);
            } else {
                System.out.println("Invalid ID token.");
            }
        } catch (GeneralSecurityException ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public GoogleUserInfoPayload getUserInfo(String accessToken) throws Exception {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Create HTTP request
        HttpRequestFactory requestFactory = transport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new com.google.api.client.http.GenericUrl(USERINFO_URL));
        request.getHeaders().setAuthorization("Bearer " + accessToken);

        // Execute request
        HttpResponse response = request.execute();
        String responseBody = response.parseAsString();
        JSONObject json = new JSONObject(responseBody);
        // Extract user info
        String givenName = json.optString("given_name");
        String familyName = json.optString("family_name");
        String picture = json.optString("picture");
        String email = json.optString("email");
        // Parse JSON response
        return new GoogleUserInfoPayload(givenName, familyName, picture, email);
    }

    public String getGoogleAccessToken(String code) throws IOException {
        // Exchange the authorization code for tokens
        logger.debug("Token request payload: CLIENT_ID={}, REDIRECT_URI={}, CODE={}", CLIENT_ID, REDIRECT_URI, code);
        String tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                CLIENT_ID,
                CLIENT_SECRET,
                code,
                REDIRECT_URI
        ).execute().getAccessToken();

        return tokenResponse;
    }
}
