package MainBackendService.dto.Flashcard;

import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.modal.FlashcardModal;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpsertFlashcardDto {
    private String id;
    private String front_image;
    private String front_sound;
    private String back_image;
    private String back_sound;
    @NotBlank()
    private String front_text;

    @NotBlank()
    private String back_text;

    private Integer position;

    private OperationType operation;

    public Boolean isValidOperation() {

        if (operation == null) return false;

        // * if operation == create => there should not be an initial flashcard's id
        if (operation.equals(OperationType.CREATE) && id != null) return false;

        // * if operation == update or delete => there should be an initial flashcard's id
        if (operation.equals(OperationType.UPDATE) || operation.equals(OperationType.DELETE))
            return id != null;

        return true;
    }

    public FlashcardModal mapToFlashcardModal() throws HttpBadRequestException {
        if (!isValidOperation()) {
            throw new HttpBadRequestException("Wrong operation type");
        }
        FlashcardModal flashcardModal = new FlashcardModal();

        // If `id` is not null, it's likely an update, so set the ID.
        if (id != null) {
            flashcardModal.setId(Integer.valueOf(id));
        }

        flashcardModal.setFront_image(front_image);
        flashcardModal.setFront_text(front_text);
        flashcardModal.setFront_sound(front_sound);

        flashcardModal.setBack_image(back_image);
        flashcardModal.setBack_text(back_text);
        flashcardModal.setBack_sound(back_sound);


        return flashcardModal;

    }

    public FlashcardRecord mapToFlashcardRecord() throws HttpBadRequestException {

        if (!isValidOperation()) {
            throw new HttpBadRequestException("Wrong operation type");
        }

        FlashcardRecord flashcardRecord = new FlashcardRecord();

        // If `id` is not null, it's likely an update, so set the ID.
        if (id != null) {
            flashcardRecord.setFlashcardId(Integer.valueOf(id));
        }

        flashcardRecord.setFlashcardFrontImage(front_image);
        flashcardRecord.setFlashcardFrontSound(front_sound);
        flashcardRecord.setFlashcardFrontText(front_text);
        flashcardRecord.setFlashcardBackImage(back_image);
        flashcardRecord.setFlashcardBackSound(back_sound);
        flashcardRecord.setFlashcardBackText(back_text);

        return flashcardRecord;
    }

}
