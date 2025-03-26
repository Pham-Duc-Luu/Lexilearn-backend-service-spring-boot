package com.MainBackendService.service.FlashcardService;

import com.MainBackendService.dto.createDto.CreateFlashcardDto;
import com.MainBackendService.modal.FlashcardModal;
import com.MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jooq.sample.model.tables.Flashcard;
import com.jooq.sample.model.tables.Vocab;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jooq.sample.model.tables.Flashcard.FLASHCARD;


@Service
public class FlashcardService {


    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SM_2_Service sm_2_service;

    /**
     * Initializes a new Flashcard for a Vocabulary in a given Desk.
     *
     * @param vocabId The ID of the Vocabulary for which the Flashcard is created.
     * @return The created FlashcardRecord.
     */
    public FlashcardRecord initFlashcardForVocabularyInDesk(int vocabId) {
        // Retrieve the vocabulary details
        Vocab vocab = Vocab.VOCAB;
        var vocabRecord = dslContext.select(vocab.VOCAB_TEXT, vocab.VOCAB_MEANING, vocab.VOCAB_IMAGE, vocab.VOCAB_DESK_ID)
                .from(vocab)
                .where(vocab.VOCAB_ID.eq(vocabId))
                .fetchOne();

        if (vocabRecord == null) {
            throw new IllegalArgumentException("Vocabulary not found for ID: " + vocabId);
        }

        // Create a new Flashcard record
        Flashcard flashcard = Flashcard.FLASHCARD;
        FlashcardRecord flashcardRecord = dslContext.newRecord(flashcard);
        flashcardRecord.setFlashcardFrontText(vocabRecord.getValue(vocab.VOCAB_TEXT));
        flashcardRecord.setFlashcardBackText(vocabRecord.getValue(vocab.VOCAB_MEANING));
        flashcardRecord.setFlashcardFrontImage(vocabRecord.getValue(vocab.VOCAB_IMAGE));
        flashcardRecord.setFlashcardVocabId(vocabId);
        flashcardRecord.setFlashcardDeskId(vocabRecord.getValue(vocab.VOCAB_DESK_ID));

        // Insert the new Flashcard into the database
        flashcardRecord.store();

        return flashcardRecord;
    }

    public Integer getFlashcardQuantityInDesk(int deskId) {
        // Fetch total count
        return dslContext.selectCount()
                .from(FLASHCARD)
                .where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId))
                .fetchOne(0, int.class);
    }

    /**
     * Initializes flashcards for all vocabularies in a given desk.
     *
     * @param deskId The ID of the desk for which the flashcards are initialized.
     * @return A list of created FlashcardRecords.
     */
    public List<FlashcardRecord> initFlashcardInDesk(int deskId) {
        // Retrieve vocabularies associated with the desk
        Vocab vocab = Vocab.VOCAB;
        Result<?> vocabRecords = dslContext.select(vocab.VOCAB_ID, vocab.VOCAB_TEXT, vocab.VOCAB_MEANING, vocab.VOCAB_IMAGE)
                .from(vocab)
                .where(vocab.VOCAB_DESK_ID.eq(deskId))
                .fetch();

        if (vocabRecords.isEmpty()) {
            throw new IllegalArgumentException("No vocabularies found for Desk ID: " + deskId);
        }

        // Initialize flashcards for each vocabulary
        Flashcard flashcard = Flashcard.FLASHCARD;
        List<FlashcardRecord> flashcardRecords = new ArrayList<>();

        for (var vocabRecord : vocabRecords) {
            FlashcardRecord flashcardRecord = dslContext.newRecord(flashcard);
            flashcardRecord.setFlashcardFrontText(vocabRecord.getValue(vocab.VOCAB_TEXT, String.class));
            flashcardRecord.setFlashcardBackText(vocabRecord.getValue(vocab.VOCAB_MEANING, String.class));
            flashcardRecord.setFlashcardFrontImage(vocabRecord.getValue(vocab.VOCAB_IMAGE, String.class));
            flashcardRecord.setFlashcardVocabId(vocabRecord.getValue(vocab.VOCAB_ID, Integer.class));
            flashcardRecord.setFlashcardDeskId(deskId);

            // Insert each flashcard into the database
            flashcardRecord.store();

            // Add to the list of created flashcards
            flashcardRecords.add(flashcardRecord);
        }

        return flashcardRecords;
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
        flashcardRecord.setFlashcardDeskPosition(getFlashcardQuantityInDesk(deskId) + 1);

        // Store the flashcard in the database
        flashcardRecord.store();

        return flashcardRecord;
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
        flashcardRecord.setFlashcardDeskPosition(flashcardModal.getDesk_position());

        // Store the flashcard in the database
        flashcardRecord.store();

        return flashcardRecord;
    }

    public FlashcardModal updateFlashcard(FlashcardModal flashcardModal) {
        Flashcard flashcard = Flashcard.FLASHCARD;

        // Find the existing flashcard record
        FlashcardRecord flashcardRecord = dslContext.selectFrom(flashcard)
                .where(flashcard.FLASHCARD_ID.eq(Integer.valueOf(flashcardModal.getId())))
                .fetchOne();

        if (flashcardRecord == null) {
            throw new RuntimeException("Flashcard not found with ID: " + flashcardModal.getId());
        }

        // Update fields if provided
        flashcardRecord.setFlashcardFrontImage(flashcardModal.getFront_image());
        flashcardRecord.setFlashcardFrontText(flashcardModal.getFront_text());
        flashcardRecord.setFlashcardFrontSound(flashcardModal.getFront_sound());
        flashcardRecord.setFlashcardBackImage(flashcardModal.getBack_image());
        flashcardRecord.setFlashcardBackText(flashcardModal.getBack_text());
        flashcardRecord.setFlashcardBackSound(flashcardModal.getBack_sound());
        flashcardRecord.setFlashcardDeskPosition(flashcardModal.getDesk_position());
        flashcardRecord.setUpdatedAt(LocalDateTime.now());

        // Update the flashcard in the database
        flashcardRecord.update();

        // Return the updated flashcard data
        return new FlashcardModal(
                String.valueOf(flashcardRecord.getFlashcardId()),
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

    public List<FlashcardModal> getFlashcardsInDesk(Integer deskId, Class<FlashcardModal> flashcardModalClass) {
        // * query conditions
        Condition condition = FLASHCARD.FLASHCARD_DESK_ID.eq(deskId);
        Flashcard flashcard = Flashcard.FLASHCARD;
        return dslContext.select(
                        flashcard.FLASHCARD_ID.as("id"),
                        flashcard.FLASHCARD_FRONT_IMAGE.as("frontImage"),
                        flashcard.FLASHCARD_FRONT_TEXT.as("frontText"),
                        flashcard.FLASHCARD_FRONT_SOUND.as("frontSound"),
                        flashcard.FLASHCARD_BACK_IMAGE.as("backImage"),
                        flashcard.FLASHCARD_BACK_TEXT.as("backText"),
                        flashcard.FLASHCARD_BACK_SOUND.as("backSound"),
                        flashcard.CREATED_AT.as("createdAt"),
                        flashcard.UPDATED_AT.as("updatedAt")
                ).from(FLASHCARD)
                .where(condition)
                .orderBy(FLASHCARD.FLASHCARD_DESK_ID.asc())
                .fetchInto(FlashcardModal.class);
    }

    public Optional<FlashcardRecord> getFlashcardById(Integer flashcardId) {
        return Optional.ofNullable(
                dslContext.selectFrom(FLASHCARD)
                        .where(FLASHCARD.FLASHCARD_ID.eq(flashcardId))
                        .fetchOne()
        );
    }

    public FlashcardModal getFlashcard(Integer flashcardId) {
        Flashcard flashcard = Flashcard.FLASHCARD;
        return dslContext.select(
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
                .where(flashcard.FLASHCARD_ID.eq(flashcardId))
                .fetchSingleInto(FlashcardModal.class);
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
                            sm_2_service.getSpacedRepetitionRecordBelongToFlashcardId(Integer.valueOf(flashcardRecord.getId()));

                    return !spacedRepetitionRecord.getSpacedRepetitionNextDay().isAfter(today);
                })
                .toList(); // Converts the filtered stream into a List
    }


    public void deleteFlashcard(Integer deskId, Integer flashcardId) {
        dslContext.delete(FLASHCARD).where(FLASHCARD.FLASHCARD_DESK_ID.eq(deskId)).execute();
    }

}

