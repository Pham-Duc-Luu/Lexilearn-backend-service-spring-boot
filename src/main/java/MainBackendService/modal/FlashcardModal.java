package MainBackendService.modal;

import MainBackendService.dto.GraphqlDto.CreateFlashcardInput;
import MainBackendService.dto.GraphqlDto.UpdateFlashcardInput;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    private Integer next_flashcard_id;
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

    public FlashcardModal(FlashcardRecord flashcardRecord) {
        this.id = flashcardRecord.getFlashcardId();
        this.front_image = flashcardRecord.getFlashcardFrontImage();
        this.front_text = flashcardRecord.getFlashcardFrontText();
        this.front_sound = flashcardRecord.getFlashcardFrontSound();
        this.back_image = flashcardRecord.getFlashcardBackImage();
        this.back_text = flashcardRecord.getFlashcardBackText();
        this.back_sound = flashcardRecord.getFlashcardBackSound();
        this.created_at = flashcardRecord.getCreatedAt() != null ? flashcardRecord.getCreatedAt().toString() : null;
        this.updated_at = flashcardRecord.getUpdatedAt() != null ? flashcardRecord.getUpdatedAt().toString() : null;
        this.next_flashcard_id = flashcardRecord.getNextFlashcardId();
        this.SM = null; // You can populate this later if needed
    }

    public FlashcardModal mapFromFlashcardRecord(FlashcardRecord flashcardRecord) {
        FlashcardModal modal = new FlashcardModal();

        modal.setId(flashcardRecord.getFlashcardId());
        modal.setFront_image(flashcardRecord.getFlashcardFrontImage());
        modal.setFront_text(flashcardRecord.getFlashcardFrontText());
        modal.setFront_sound(flashcardRecord.getFlashcardFrontSound());
        modal.setBack_image(flashcardRecord.getFlashcardBackImage());
        modal.setBack_text(flashcardRecord.getFlashcardBackText());
        modal.setBack_sound(flashcardRecord.getFlashcardBackSound());
        modal.setCreated_at(flashcardRecord.getCreatedAt() != null ? flashcardRecord.getCreatedAt().toString() : null);
        modal.setUpdated_at(flashcardRecord.getUpdatedAt() != null ? flashcardRecord.getUpdatedAt().toString() : null);
        modal.setNext_flashcard_id(flashcardRecord.getNextFlashcardId());
        return modal;
    }

    public FlashcardRecord mapToFlashcardRecord() {
        FlashcardRecord record = new FlashcardRecord();
        record.setFlashcardId(this.id);
        record.setFlashcardFrontImage(this.front_image);
        record.setFlashcardFrontText(this.front_text);
        record.setFlashcardFrontSound(this.front_sound);
        record.setFlashcardBackImage(this.back_image);
        record.setFlashcardBackText(this.back_text);
        record.setFlashcardBackSound(this.back_sound);
        record.setNextFlashcardId(this.next_flashcard_id);

        // If your table has created_at/updated_at as `LocalDateTime`
        if (this.created_at != null) {
            record.setCreatedAt(java.time.LocalDateTime.parse(this.created_at));
        }

        if (this.updated_at != null) {
            record.setUpdatedAt(java.time.LocalDateTime.parse(this.updated_at));
        }

        return record;

    }

}
