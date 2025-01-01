package com.MainBackendService.service.FlashcardService;

import com.MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import com.MainBackendService.modal.FlashcardModal;
import com.MainBackendService.modal.SMModal;
import com.MainBackendService.service.SpacedRepetitionSerivce.SM_2_GQLService;
import com.jooq.sample.model.tables.Flashcard;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardGQLService {


    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SM_2_GQLService sm_2_gqlService;

    /**
     * Fetch flashcards with pagination and desk filter.
     *
     * @param skip   The offset for pagination.
     * @param limit  The number of items per page.
     * @param deskId The ID of the desk.
     * @return FlashcardPaginationResult containing flashcards and pagination details.
     */
    public FlashcardPaginationResult getFlashcards(Integer skip, Integer limit, Integer deskId) {
        Flashcard flashcard = Flashcard.FLASHCARD;

        // Fetch total count
        int total = dslContext.selectCount()
                .from(flashcard)
                .where(flashcard.FLASHCARD_DESK_ID.eq(deskId))
                .fetchOne(0, int.class);

        List<FlashcardModal> flashcards = dslContext.select(
                        flashcard.FLASHCARD_ID.as("id"),
                        flashcard.FLASHCARD_FRONT_IMAGE.as("frontImage"),
                        flashcard.FLASHCARD_FRONT_TEXT.as("frontText"),
                        flashcard.FLASHCARD_FRONT_SOUND.as("frontSound"),
                        flashcard.FLASHCARD_BACK_IMAGE.as("backImage"),
                        flashcard.FLASHCARD_BACK_TEXT.as("backText"),
                        flashcard.FLASHCARD_BACK_SOUND.as("backSound"),
                        flashcard.CREATED_AT.as("createdAt"),
                        flashcard.UPDATED_AT.as("updatedAt")
                )
                .from(flashcard)
                .where(flashcard.FLASHCARD_DESK_ID.eq(deskId))
                .offset(skip != null ? skip : 0)
                .limit(limit != null ? limit : 30)
                .fetchInto(FlashcardModal.class);
        // Populate SM field
        for (FlashcardModal flashcardItem : flashcards) {
            SMModal sm = sm_2_gqlService.getSpacedRepetitionWithFlashcardId(Integer.valueOf(flashcardItem.getId()));
            flashcardItem.setSM(sm);
        }

        return new FlashcardPaginationResult(flashcards, total, skip, limit);
    }
}
