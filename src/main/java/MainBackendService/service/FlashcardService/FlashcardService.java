package MainBackendService.service.FlashcardService;

import MainBackendService.dto.createDto.CreateFlashcardDto;
import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.modal.FlashcardModal;
import MainBackendService.service.DeskService.DeskFlashcardsLinkedListOperation;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jooq.sample.model.tables.Flashcard;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jooq.sample.model.tables.Flashcard.FLASHCARD;


@Service
public class FlashcardService {
    Logger logger = LogManager.getLogger(FlashcardService.class);


    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SM_2_Service sm_2_service;

    @Autowired
    private DeskService deskService;


    @Autowired
    private DeskFlashcardsLinkedListOperation deskFlashcardsLinkedListOperation;


    public List<FlashcardRecord> createFlashcards(List<FlashcardRecord> flashcardRecords) {
        LocalDateTime now = LocalDateTime.now();
        flashcardRecords.forEach(record -> {
            record.setCreatedAt(now);
            record.setUpdatedAt(now);
        });

        int[] inserted = dslContext
                .batchInsert(flashcardRecords)
                .execute();


        return flashcardRecords;
    }

    public List<FlashcardRecord> upsertFlashcards(List<FlashcardRecord> flashcardRecords) {
        LocalDateTime now = LocalDateTime.now();

        flashcardRecords.forEach(record -> {
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(now);
            }
            record.setUpdatedAt(now);
        });

        flashcardRecords.forEach(record ->
                dslContext.insertInto(FLASHCARD) // Use your table reference
                        .set(record)
                        .onDuplicateKeyUpdate()
                        .set(FLASHCARD.FLASHCARD_FRONT_TEXT, record.getFlashcardFrontText())
                        .set(FLASHCARD.FLASHCARD_BACK_TEXT, record.getFlashcardBackText())
                        .set(FLASHCARD.FLASHCARD_FRONT_IMAGE, record.getFlashcardFrontImage())
                        .set(FLASHCARD.FLASHCARD_BACK_IMAGE, record.getFlashcardBackImage())
                        .set(FLASHCARD.FLASHCARD_FRONT_SOUND, record.getFlashcardFrontSound())
                        .set(FLASHCARD.FLASHCARD_BACK_SOUND, record.getFlashcardBackSound())
                        .set(FLASHCARD.UPDATED_AT, record.getUpdatedAt())
                        .execute()
        );

        return flashcardRecords;
    }


    public Integer getFlashcardQuantityInDesk(int deskId) {
        // Fetch total count
        return dslContext.selectCount()
                .from(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId))
                .fetchOne(0, int.class);
    }

    /**
     * Creates a new Flashcard in a given Desk based on provided FlashcardDto.
     *
     * @param deskId             The ID of the desk in which the flashcard will be created.
     * @param createFlashcardDto The data transfer object containing the flashcard details.
     * @return The created FlashcardRecord.
     */
    public FlashcardRecord createFlashcardInDesk(int deskId, CreateFlashcardDto createFlashcardDto) {
        // Validate required fields in FlashcardDto
        if (createFlashcardDto.getFront_text() == null || createFlashcardDto.getFront_text().isBlank()) {
            throw new IllegalArgumentException("Front text cannot be null or blank.");
        }

        if (createFlashcardDto.getBack_text() == null || createFlashcardDto.getBack_text().isBlank()) {
            throw new IllegalArgumentException("Back text cannot be null or blank.");
        }

        // Create a new Flashcard record
        Flashcard flashcard = Flashcard.FLASHCARD;
        FlashcardRecord flashcardRecord = dslContext.newRecord(flashcard);

        flashcardRecord.setFlashcardDeskId(deskId);
        flashcardRecord.setFlashcardFrontImage(createFlashcardDto.getFront_image());
        flashcardRecord.setFlashcardFrontSound(createFlashcardDto.getFront_sound());
        flashcardRecord.setFlashcardFrontText(createFlashcardDto.getFront_text());
        flashcardRecord.setFlashcardBackImage(createFlashcardDto.getBack_image());
        flashcardRecord.setFlashcardBackSound(createFlashcardDto.getBack_sound());

        flashcardRecord.setFlashcardBackText(createFlashcardDto.getBack_text());

        // Store the flashcard in the database
        flashcardRecord.store();

        return flashcardRecord;
    }


    public List<Integer> AutoRepairFlashcardLinkedlist(Integer deskId) throws HttpResponseException {

        // * get all the flashcard in desk
        List<FlashcardRecord> allFlashcard = dslContext
                .select(FLASHCARD.FLASHCARD_ID)
                .select(FLASHCARD.NEXT_FLASHCARD_ID)
                .from(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId))
                .fetchInto(FlashcardRecord.class);

        if (allFlashcard == null) {
            throw new HttpNotFoundException("This desk don't have any flashcards");
        }

        List<Integer> mutableFlashcardList = allFlashcard.stream().map(FlashcardRecord::getFlashcardId).collect(Collectors.toCollection(ArrayList::new));


        // * get the desk
        DeskRecord deskRecord = deskService.getDeskById(deskId);

        List<Integer> flashcardID_linkedList = deskFlashcardsLinkedListOperation.linkedListTraverse(deskRecord);

        // * re-check the desk's linked list
        // * check if the remain linked list in exist
        if (deskRecord.getDeskStartFlashcardId() != null && flashcardID_linkedList != null) {
            // * check if there are any remain linked list of the desk


            // * remove all linked list item in the list
            // * make sure to process the un linked list item
            mutableFlashcardList.removeAll(flashcardID_linkedList);

            // * add the last item in the linked list to the array
            mutableFlashcardList.addFirst(flashcardID_linkedList.getLast());
        }


        List<Integer> unique_flashcard = new ArrayList<>(new LinkedHashSet<>(mutableFlashcardList))
                .stream()
                .filter(Objects::nonNull)                  // removes nulls
                .distinct()                                // removes duplicates
                .collect(Collectors.toList());

        for (int i = 1; i < unique_flashcard.size(); i++) {
            // * update the next ids to create linked list
            getFlashcardInDesk(deskId, unique_flashcard.get(i - 1)).setNextFlashcardId(unique_flashcard.get(i)).store();

            flashcardID_linkedList.add(unique_flashcard.get(i));
        }


        return flashcardID_linkedList;

    }

    public FlashcardRecord createFlashcard(int deskId, FlashcardModal flashcardModal) {
        // Validate required fields in FlashcardDto
        if (flashcardModal.getFront_text() == null || flashcardModal.getFront_text().isBlank()) {
            throw new IllegalArgumentException("Front text cannot be null or blank.");
        }

        if (flashcardModal.getBack_text() == null || flashcardModal.getBack_text().isBlank()) {
            throw new IllegalArgumentException("Back text cannot be null or blank.");
        }

        // Create a new Flashcard record
        Flashcard flashcard = Flashcard.FLASHCARD;
        FlashcardRecord flashcardRecord = dslContext.newRecord(flashcard);

        flashcardRecord.setFlashcardDeskId(deskId);
        flashcardRecord.setFlashcardFrontImage(flashcardModal.getFront_image());
        flashcardRecord.setFlashcardFrontSound(flashcardModal.getFront_sound());
        flashcardRecord.setFlashcardFrontText(flashcardModal.getFront_text());
        flashcardRecord.setFlashcardBackImage(flashcardModal.getBack_image());
        flashcardRecord.setFlashcardBackSound(flashcardModal.getBack_sound());
        flashcardRecord.setFlashcardBackText(flashcardModal.getBack_text());
        flashcardRecord.setNextFlashcardId(flashcardModal.getNext_flashcard_id());

        // Store the flashcard in the database
        flashcardRecord.store();

        return flashcardRecord;
    }

    public FlashcardModal updateFlashcard(Integer deskId, Integer flashcardId, FlashcardModal flashcardModal) throws HttpBadRequestException {
        // Find the existing flashcard record
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId).and(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId)))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new HttpBadRequestException("Flashcard not found with ID: " + flashcardId);
        }

        // Update fields if provided
        flashcardRecord.setFlashcardFrontImage(flashcardModal.getFront_image());
        flashcardRecord.setFlashcardFrontText(flashcardModal.getFront_text());
        flashcardRecord.setFlashcardFrontSound(flashcardModal.getFront_sound());
        flashcardRecord.setFlashcardBackImage(flashcardModal.getBack_image());
        flashcardRecord.setFlashcardBackText(flashcardModal.getBack_text());
        flashcardRecord.setFlashcardBackSound(flashcardModal.getBack_sound());
        flashcardRecord.setUpdatedAt(LocalDateTime.now());

        // Update the flashcard in the database
        flashcardRecord.update();

        // Return the updated flashcard data
        return new FlashcardModal(
                flashcardRecord
        );
    }

    public List<FlashcardRecord> getFlashcardsInDesk(Integer deskId, Integer limit, Integer offset) {
        // * query conditions
        Condition condition = FLASHCARD.FLASHCARD_DESK_ID.eq(deskId);
        return dslContext.selectFrom(FLASHCARD)
                .where(condition).limit(limit).offset(offset)
                .fetchInto(FlashcardRecord.class);

    }

    public List<FlashcardRecord> getFlashcardsInDesk(Integer deskId) {
        // * query conditions
        Condition condition = FLASHCARD.FLASHCARD_DESK_ID.eq(deskId);
        return dslContext.selectFrom(FLASHCARD)
                .where(condition)
                .fetchInto(FlashcardRecord.class);

    }


    public List<FlashcardRecord> getFlashcardsInDesk(Integer deskId, Field field) {
        // * query conditions
        Condition condition = FLASHCARD.FLASHCARD_DESK_ID.eq(deskId);
        return dslContext
                .select(field)
                .from(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId))
                .fetchInto(FlashcardRecord.class);

    }

    public FlashcardRecord getFlashcardInDesk(Integer deskId, Integer flashcardId) {
        // * query conditions
        Condition condition = FLASHCARD.FLASHCARD_DESK_ID.eq(deskId).and(FLASHCARD.FLASHCARD_ID.eq(flashcardId));
        return dslContext.selectFrom(FLASHCARD)
                .where(condition)
                .fetchOne();
    }

    public FlashcardRecord patchUpdateFlashcard(Integer deskId, Integer flashcardId, FlashcardModal flashcardModal) throws HttpBadRequestException {

        // Find the existing flashcard record
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId).and(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId)))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new HttpBadRequestException("Flashcard not found with ID: " + flashcardId);
        }

        // Conditionally update fields if not null
        if (flashcardModal.getFront_image() != null) {
            flashcardRecord.setFlashcardFrontImage(flashcardModal.getFront_image());
        }

        if (flashcardModal.getFront_text() != null) {
            flashcardRecord.setFlashcardFrontText(flashcardModal.getFront_text());
        }

        if (flashcardModal.getFront_sound() != null) {
            flashcardRecord.setFlashcardFrontSound(flashcardModal.getFront_sound());
        }

        if (flashcardModal.getBack_image() != null) {
            flashcardRecord.setFlashcardBackImage(flashcardModal.getBack_image());
        }

        if (flashcardModal.getBack_text() != null) {
            flashcardRecord.setFlashcardBackText(flashcardModal.getBack_text());
        }

        if (flashcardModal.getBack_sound() != null) {
            flashcardRecord.setFlashcardBackSound(flashcardModal.getBack_sound());
        }

        // Always update updatedAt to now
        flashcardRecord.setUpdatedAt(LocalDateTime.now());

        // Store the updated record
        flashcardRecord.store();

        return flashcardRecord;
    }


    public List<FlashcardModal> getFlashcardsInDesk(Integer deskId, Class<FlashcardModal> flashcardModalClass) {
        return getFlashcardsInDesk(deskId).stream().map(item -> new FlashcardModal(item)).toList();
    }

    public Optional<FlashcardRecord> getFlashcardById(Integer flashcardId) {
        return Optional.ofNullable(
                dslContext.selectFrom(FLASHCARD)
                        .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                        .fetchOne()
        );
    }

    public FlashcardModal getFlashcard(Integer flashcardId) throws HttpNotFoundException {
        FlashcardRecord flashcardRecord = dslContext.selectFrom(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId)).fetchOne();

        if (flashcardRecord == null) {
            throw new HttpNotFoundException("Flashcard not found");
        }

        return new FlashcardModal().mapFromFlashcardRecord(flashcardRecord);
    }

    public List<FlashcardRecord> getNeedToReviewFlashcards(Integer deskId) {
        List<FlashcardRecord> flashcardRecordList = getFlashcardsInDesk(deskId);

        // Get today's date
        LocalDate today = LocalDate.now();

        // Filter flashcards that need review
        return flashcardRecordList.stream()
                .filter(flashcardRecord -> {
                    SpacedRepetitionRecord spacedRepetitionRecord =
                            sm_2_service.getSpacedRepetitionRecordBelongToFlashcardId(flashcardRecord.getFlashcardId());

                    return !spacedRepetitionRecord.getSpacedRepetitionNextDay().isAfter(today);
                })
                .toList(); // Converts the filtered stream into a List
    }

    public List<FlashcardModal> getNeedToReviewFlashcards(Integer deskId, Class<FlashcardModal> flashcardModalClass) {
        List<FlashcardModal> flashcardModalList = getFlashcardsInDesk(deskId, flashcardModalClass);

        // Get today's date
        LocalDate today = LocalDate.now();

        // Filter flashcards that need review
        return flashcardModalList.stream()
                .filter(flashcardRecord -> {
                    SpacedRepetitionRecord spacedRepetitionRecord =
                            sm_2_service.getSpacedRepetitionRecordBelongToFlashcardId(flashcardRecord.getId());

                    return !spacedRepetitionRecord.getSpacedRepetitionNextDay().isAfter(today);
                })
                .toList(); // Converts the filtered stream into a List
    }

    public void updateFlashcardFrontImageUrl(Integer flashcardId, String url) {
        dslContext.update(FLASHCARD).set(FLASHCARD.FLASHCARD_FRONT_IMAGE, url).where(FLASHCARD.FLASHCARD_ID.eq(flashcardId)).execute();
    }

    public void updateFlashcardFrontSoundUrl(Integer flashcardId, String url) {
        dslContext.update(FLASHCARD).set(FLASHCARD.FLASHCARD_FRONT_SOUND, url).where(FLASHCARD.FLASHCARD_ID.eq(flashcardId)).execute();
    }


    public void updateFlashcardBackImageUrl(Integer flashcardId, String url) {
        dslContext.update(FLASHCARD).set(FLASHCARD.FLASHCARD_BACK_IMAGE, url).where(FLASHCARD.FLASHCARD_ID.eq(flashcardId)).execute();
    }


    public void deleteFlashcard(Integer deskId, Integer flashcardId) {
        dslContext.delete(FLASHCARD).where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId)).execute();
    }

}


