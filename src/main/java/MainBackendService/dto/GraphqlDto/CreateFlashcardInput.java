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


}
