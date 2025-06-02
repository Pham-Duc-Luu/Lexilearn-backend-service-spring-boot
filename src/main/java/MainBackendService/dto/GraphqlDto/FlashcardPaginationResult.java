package MainBackendService.dto.GraphqlDto;


import MainBackendService.modal.FlashcardModal;
import lombok.Data;

import java.util.List;

@Data
public class FlashcardPaginationResult {
    private List<FlashcardModal> flashcards;
    private int total;
    private int skip;
    private int limit;

    public FlashcardPaginationResult(List<FlashcardModal> flashcards, int total, int skip, int limit) {
        this.flashcards = flashcards;
        this.total = total;
        this.skip = skip;
        this.limit = limit;
    }

}
