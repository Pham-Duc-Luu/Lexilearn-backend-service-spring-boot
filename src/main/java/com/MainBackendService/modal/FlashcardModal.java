package com.MainBackendService.modal;

import com.MainBackendService.dto.GraphqlDto.CreateFlashcardInput;
import com.MainBackendService.dto.GraphqlDto.UpdateFlashcardInput;

public class FlashcardModal {
    private Integer id;
    private String front_image;
    private String front_text;
    private String front_sound;
    private String back_image;
    private String back_text;
    private String back_sound;
    private String created_at;
    private String updated_at;
    private Integer desk_position;
    private SMModal SM; // Add this field

    public FlashcardModal(Integer id, String front_image, String front_text, String front_sound, String back_image, String back_text, String back_sound, String created_at, String updated_at, Integer desk_position) {
        this.id = id;
        this.front_image = front_image;
        this.front_text = front_text;
        this.front_sound = front_sound;
        this.back_image = back_image;
        this.back_text = back_text;
        this.back_sound = back_sound;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.desk_position = desk_position;
    }

    public FlashcardModal(CreateFlashcardInput createFlashcardInput) {
        this.front_image = createFlashcardInput.getFront_image();
        this.front_text = createFlashcardInput.getFront_text();
        this.front_sound = createFlashcardInput.getFront_sound();
        this.back_image = createFlashcardInput.getBack_image();
        this.back_text = createFlashcardInput.getBack_text();
        this.back_sound = createFlashcardInput.getBack_sound();
    }

    public FlashcardModal(UpdateFlashcardInput updateFlashcardInput) {


        this.id = updateFlashcardInput.getId();

        this.front_image = updateFlashcardInput.getFront_image();
        this.front_text = updateFlashcardInput.getFront_text();
        this.front_sound = updateFlashcardInput.getFront_sound();
        this.back_image = updateFlashcardInput.getBack_image();
        this.back_text = updateFlashcardInput.getBack_text();
        this.back_sound = updateFlashcardInput.getBack_sound();
    }


    public FlashcardModal(Integer id, String front_image, String front_text, String front_sound, String back_image, String back_text, String back_sound, String created_at, String updated_at) {
        this.id = id;
        this.front_image = front_image;
        this.front_text = front_text;
        this.front_sound = front_sound;
        this.back_image = back_image;
        this.back_text = back_text;
        this.back_sound = back_sound;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Integer getDesk_position() {
        return desk_position;
    }

    public void setDesk_position(Integer desk_position) {
        this.desk_position = desk_position;
    }

    public SMModal getSM() {
        return SM;
    }

    public void setSM(SMModal SM) {
        this.SM = SM;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFront_image() {
        return front_image;
    }

    public void setFront_image(String front_image) {
        this.front_image = front_image;
    }

    public String getFront_text() {
        return front_text;
    }

    public void setFront_text(String front_text) {
        this.front_text = front_text;
    }

    public String getFront_sound() {
        return front_sound;
    }

    public void setFront_sound(String front_sound) {
        this.front_sound = front_sound;
    }

    public String getBack_image() {
        return back_image;
    }

    public void setBack_image(String back_image) {
        this.back_image = back_image;
    }

    public String getBack_text() {
        return back_text;
    }

    public void setBack_text(String back_text) {
        this.back_text = back_text;
    }

    public String getBack_sound() {
        return back_sound;
    }

    public void setBack_sound(String back_sound) {
        this.back_sound = back_sound;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
