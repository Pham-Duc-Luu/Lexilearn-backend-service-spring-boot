package com.MainBackendService.service.FlashcardService;

import com.jooq.sample.model.tables.Flashcard;
import org.jooq.DSLContext;

public class FlashcardBelongToUser {
    private final DSLContext dslContext;
    private final Integer USER_ID;
    //    private final Condition flashcardBelongToUser;
    private final Flashcard flashcard = Flashcard.FLASHCARD;

    public FlashcardBelongToUser(Integer USER_ID, DSLContext dslContext) {
        this.USER_ID = USER_ID;
        this.dslContext = dslContext;
    }

}
