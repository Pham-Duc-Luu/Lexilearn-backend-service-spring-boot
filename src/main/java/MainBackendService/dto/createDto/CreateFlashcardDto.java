package MainBackendService.dto.createDto;

import MainBackendService.dto.FlashcardDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFlashcardDto {
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

    private Integer position;

    public CreateFlashcardDto(String front_image, String front_sound, String front_text, String back_image, String back_sound, String back_text) {
        this.front_image = front_image;
        this.front_sound = front_sound;
        this.front_text = front_text;
        this.back_image = back_image;
        this.back_sound = back_sound;
        this.back_text = back_text;
    }

    public CreateFlashcardDto(FlashcardDto flashcardDto) {
        this.front_image = flashcardDto.getFront_image();
        this.front_sound = flashcardDto.getFront_sound();
        this.front_text = flashcardDto.getFront_text();
        this.back_image = flashcardDto.getBack_image();
        this.back_sound = flashcardDto.getBack_sound();
        this.back_text = flashcardDto.getBack_text();
    }


}
