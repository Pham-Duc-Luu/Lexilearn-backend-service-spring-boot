package MainBackendService.dto.GraphqlDto;

import lombok.Data;

@Data
public class CreateFlashcardInput {
    private Integer desk_id;
    private String front_image;
    private String front_text;
    private String front_sound;
    private String back_image;
    private String back_text;
    private String back_sound;

    public CreateFlashcardInput() {
    }

    public CreateFlashcardInput(UpdateFlashcardInput updateFlashcardInput) {
        this.desk_id = updateFlashcardInput.getDesk_id();
        this.front_image = updateFlashcardInput.getFront_image();
        this.front_text = updateFlashcardInput.getFront_text();
        this.front_sound = updateFlashcardInput.getFront_sound();
        this.back_image = updateFlashcardInput.getBack_image();
        this.back_text = updateFlashcardInput.getBack_text();
        this.back_sound = updateFlashcardInput.getBack_sound();
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


}
