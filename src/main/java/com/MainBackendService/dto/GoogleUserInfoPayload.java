package com.MainBackendService.dto;

import jakarta.validation.constraints.NotNull;

public class GoogleUserInfoPayload {
    @NotNull
    private String givenName;
    @NotNull
    private String familyName;
    @NotNull
    private String picture;
    @NotNull
    private String email;

    public GoogleUserInfoPayload(String givenName, String familyName, String picture, String email) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
}
