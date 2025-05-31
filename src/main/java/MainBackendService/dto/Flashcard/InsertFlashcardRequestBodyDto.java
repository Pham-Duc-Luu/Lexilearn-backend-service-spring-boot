package MainBackendService.dto.Flashcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InsertFlashcardRequestBodyDto {
    @Valid
    private InsertFlashcardDto data;

    @NotNull(message = "desk's id in not exist!")
    private Integer deskId;


    public boolean hasRightOperationType() {
        return data.getOperation().equals(OperationType.CREATE);
    }
}
