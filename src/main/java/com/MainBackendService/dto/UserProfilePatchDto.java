package com.MainBackendService.dto;

import jakarta.validation.constraints.Size;

public class UserProfilePatchDto {

    @Size(min = 8, max = 50, message = "Username must be between 3 and 50 characters")
    public String user_name;
    private String name;
    private String avatar;
    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
