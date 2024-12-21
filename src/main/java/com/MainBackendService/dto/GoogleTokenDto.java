package com.MainBackendService.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class GoogleTokenDto {
    @NotNull
    @NotEmpty
    private String token;

    public GoogleTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
