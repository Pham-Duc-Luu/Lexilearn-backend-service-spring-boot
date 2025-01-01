package com.MainBackendService.modal;

public class UserModal {

    private String id;
    private String name;
    private String email;
    private String avatar;
    private String thumbnail;
    private String provider;
    public UserModal(String id, String name, String email, String avatar, String thumbnail, String provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.provider = provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}
