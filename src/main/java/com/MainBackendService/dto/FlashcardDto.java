package com.MainBackendService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlashcardDto {

    private String id;
    private String front_image;
    private String front_sound;
    @NotBlank()
    @NotNull()
    private String front_text;
    private String back_image;
    private String back_sound;
    @NotBlank()
    @NotNull()
    private String back_text;
    private String deskId;

    public FlashcardDto(String id, String front_image, String front_sound, String front_text, String back_image, String back_sound, String back_text) {
        this.id = id;
        this.front_image = front_image;
        this.front_sound = front_sound;
        this.front_text = front_text;
        this.back_image = back_image;
        this.back_sound = back_sound;
        this.back_text = back_text;
    }

    public String getDeskId() {
        return deskId;
    }

    public void setDeskId(String deskId) {
        this.deskId = deskId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFront_image() {
        return front_image;
    }

    public void setFront_image(String front_image) {
        this.front_image = front_image;
    }

    public String getFront_sound() {
        return front_sound;
    }

    public void setFront_sound(String front_sound) {
        this.front_sound = front_sound;
    }

    public String getFront_text() {
        return front_text;
    }

    public void setFront_text(String front_text) {
        this.front_text = front_text;
    }

    public String getBack_image() {
        return back_image;
    }

    public void setBack_image(String back_image) {
        this.back_image = back_image;
    }

    public String getBack_sound() {
        return back_sound;
    }

    public void setBack_sound(String back_sound) {
        this.back_sound = back_sound;
    }

    public String getBack_text() {
        return back_text;
    }

    public void setBack_text(String back_text) {
        this.back_text = back_text;
    }
}
