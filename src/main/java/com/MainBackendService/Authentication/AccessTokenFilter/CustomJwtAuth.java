package com.MainBackendService.Authentication.AccessTokenFilter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomJwtAuth extends AbstractAuthenticationToken {

    private final String userEmail;
    private final String userName;

    public CustomJwtAuth(String userEmail, String userName, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userEmail = userEmail;
        this.userName = userName;
        this.setAuthenticated(true); // Mark as authenticated
    }

    @Override
    public Object getPrincipal() {
        return this; // Return the entire CustomJwtAuth object
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials required for JWT-based auth
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }
}
