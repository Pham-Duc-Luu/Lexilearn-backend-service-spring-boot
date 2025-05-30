package MainBackendService.service.FlashcardService;

import MainBackendService.dto.GraphqlDto.CreateFlashcardInput;
import MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import MainBackendService.dto.GraphqlDto.UpdateFlashcardInput;
import MainBackendService.modal.FlashcardModal;
import MainBackendService.modal.SMModal;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_GQLService;
import com.jooq.sample.model.tables.Flashcard;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.jooq.sample.model.tables.Flashcard.FLASHCARD;

@Service
public class FlashcardGQLService {


    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SM_2_GQLService sm_2_gqlService;

    @Autowired
    private DeskService deskService;

    @Autowired
    private FlashcardService flashcardService;

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

        List<FlashcardRecord> flashcards = dslContext.selectFrom(FLASHCARD)
                .where(flashcard.FLASHCARD_DESK_ID.eq(deskId))
                .offset(skip != null ? skip : 0)
                .limit(limit != null ? limit : 30)
                .fetch();
    

        List<FlashcardModal> flashcardModalList = flashcards.stream().map(item -> {
            FlashcardModal flashcardModal = new FlashcardModal(item);
            SMModal sm = sm_2_gqlService.getSpacedRepetitionWithFlashcardId(flashcardModal.getId());
            flashcardModal.setSM(sm);

            return flashcardModal;
        }).toList();

        return new FlashcardPaginationResult(flashcardModalList, total, skip, limit);
    }

    public FlashcardModal createFlashcard(CreateFlashcardInput input) {

        Flashcard flashcard = Flashcard.FLASHCARD;
        // Create a new Flashcard record
        FlashcardRecord flashcardRecord = dslContext.newRecord(flashcard);

        flashcardRecord.setFlashcardDeskId(input.getDesk_id());
        flashcardRecord.setFlashcardFrontImage(input.getFront_image());
        flashcardRecord.setFlashcardFrontSound(input.getFront_sound());
        flashcardRecord.setFlashcardFrontText(input.getFront_text());
        flashcardRecord.setFlashcardBackImage(input.getBack_image());
        flashcardRecord.setFlashcardBackSound(input.getBack_sound());
        flashcardRecord.setFlashcardBackText(input.getBack_text());
        flashcardRecord.setCreatedAt(LocalDateTime.now());
        flashcardRecord.setUpdatedAt(LocalDateTime.now());

        // Store the flashcard in the database
        flashcardRecord.store();
        // Return the created flashcard data
        return new FlashcardModal(
                flashcardRecord.getFlashcardId(),
                flashcardRecord.getFlashcardFrontImage(),
                flashcardRecord.getFlashcardFrontText(),
                flashcardRecord.getFlashcardFrontSound(),
                flashcardRecord.getFlashcardBackImage(),
                flashcardRecord.getFlashcardBackText(),
                flashcardRecord.getFlashcardBackSound(),
                flashcardRecord.getCreatedAt().toString(),
                flashcardRecord.getUpdatedAt().toString()
        );
    }

    public FlashcardModal updateFlashcard(UpdateFlashcardInput input) {
        Flashcard flashcard = Flashcard.FLASHCARD;

        // Find the existing flashcard record
        FlashcardRecord flashcardRecord = dslContext.selectFrom(flashcard)
                .where(flashcard.FLASHCARD_ID.eq(input.getId()))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new RuntimeException("Flashcard not found with ID: " + input.getId());
        }

        // Update fields if provided
        flashcardRecord.setFlashcardFrontImage(input.getFront_image());
        flashcardRecord.setFlashcardFrontText(input.getFront_text());
        flashcardRecord.setFlashcardFrontSound(input.getFront_sound());
        flashcardRecord.setFlashcardBackImage(input.getBack_image());
        flashcardRecord.setFlashcardBackText(input.getBack_text());
        flashcardRecord.setFlashcardBackSound(input.getBack_sound());
        flashcardRecord.setUpdatedAt(LocalDateTime.now());

        // Update the flashcard in the database
        flashcardRecord.update();

        // Return the updated flashcard data
        return new FlashcardModal(
                flashcardRecord.getFlashcardId(),
                flashcardRecord.getFlashcardFrontImage(),
                flashcardRecord.getFlashcardFrontText(),
                flashcardRecord.getFlashcardFrontSound(),
                flashcardRecord.getFlashcardBackImage(),
                flashcardRecord.getFlashcardBackText(),
                flashcardRecord.getFlashcardBackSound(),
                flashcardRecord.getCreatedAt().toString(),
                flashcardRecord.getUpdatedAt().toString()

        );
    }
//
//    public FlashcardModal deleteFlashcard() {
//        // * update the desk's flashcard list
//
//    }

//    public  Integer deleteFlashcard(Integer flashcardId){
//        new FlashcardBelongToDeskService()
//    }

}
