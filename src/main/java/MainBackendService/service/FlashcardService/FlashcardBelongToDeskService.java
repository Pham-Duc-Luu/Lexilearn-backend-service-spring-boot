package MainBackendService.service.FlashcardService;

import MainBackendService.exception.HttpResponseException;
import com.jooq.sample.model.tables.Flashcard;
import org.jooq.Condition;
import org.jooq.DSLContext;

public class FlashcardBelongToDeskService {
    private final DSLContext dslContext;
    private final Integer DESK_ID;
    private final Condition flashcardInDeskOnlyCondition;
    private final Flashcard flashcard = Flashcard.FLASHCARD;

    public FlashcardBelongToDeskService(Integer DESK_ID, DSLContext dslContext) {
        this.DESK_ID = DESK_ID;
        this.dslContext = dslContext;
        // * define this query so only flashcard in side a desk can be effected
        // * as this to every query
        this.flashcardInDeskOnlyCondition = flashcard.FLASHCARD_DESK_ID.eq(DESK_ID);
    }

    public void deleteFlashcard(Integer flashcardId) throws HttpResponseException {
        dslContext.delete(flashcard).where(flashcardInDeskOnlyCondition.and(flashcard.FLASHCARD_ID.eq(flashcardId))).execute();
    }

}
