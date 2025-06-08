package MainBackendService.dto.Flashcard;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFlashcardRequestBodyDto extends InsertFlashcardRequestBodyDto {

    @Valid
    private UpdateFlashcardDto data;
    
    @NotNull(message = "flashcard's id in not exist!")
    private Integer flashcardId;

    @Override
    public boolean hasRightOperationType() {
        return getData().getOperation().equals(OperationType.UPDATE);
    }

}
