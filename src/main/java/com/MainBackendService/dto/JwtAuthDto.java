package com.MainBackendService.dto;

import jakarta.validation.constraints.NotNull;

public class JwtAuthDto {
    public String getRefresh_token() {
        return refresh_token;
    }

    public JwtAuthDto(String refresh_token, String access_token){
        this.access_token = access_token;
        this.refresh_token= refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @NotNull
    private String refresh_token;

    @NotNull
    private String access_token;
}
