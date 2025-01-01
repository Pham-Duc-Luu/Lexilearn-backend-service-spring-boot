package com.MainBackendService.service.SpacedRepetitionSerivce;

import com.MainBackendService.modal.SMModal;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jooq.sample.model.tables.SpacedRepetition.SPACED_REPETITION;

@Service
public class SM_2_GQLService {

    @Autowired
    private DSLContext dslContext;

    public SMModal getSpacedRepetitionWithFlashcardId(Integer flashcardId) {

        return dslContext.select(
                        SPACED_REPETITION.SPACED_REPETITION_ID.as("id"),
                        SPACED_REPETITION.SPACED_REPETITION_NAME.as("name"),
                        SPACED_REPETITION.SPACED_REPETITION_COUNT.as("count"),
                        SPACED_REPETITION.SPACED_REPETITION_EASINESS_FACTOR.as("easinessFactor"),
                        SPACED_REPETITION.SPACED_REPETITION_INTERVAL.as("interval"),
                        SPACED_REPETITION.SPACED_REPETITION_NEXT_DAY.as("nextDay")
                ).from(SPACED_REPETITION).
                where(SPACED_REPETITION.SPACED_REPETITION_FLASHCARD_ID.eq(flashcardId))
                .fetchOneInto(SMModal.class);

    }
}
