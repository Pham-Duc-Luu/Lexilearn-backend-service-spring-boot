package com.MainBackendService.model;

public enum UserAuthProvider {
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK"),
    LOCAL("LOCAL");

    private final String provider;

    UserAuthProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}
