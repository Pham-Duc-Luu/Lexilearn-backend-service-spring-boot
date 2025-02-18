package com.MainBackendService.service.SpacedRepetitionSerivce;

import com.jooq.sample.model.enums.SpacedRepetitionSpacedRepetitionName;
import com.jooq.sample.model.tables.SpacedRepetition;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.jooq.sample.model.tables.SpacedRepetition.SPACED_REPETITION;

@Service
public class SM_2_Service {

    @Autowired
    private DSLContext dslContext;


    /**
     * Initializes a spaced repetition (SM-0) record for a given flashcard.
     *
     * @param flashcardId The ID of the flashcard for which the SM-0 is initialized.
     * @return The created SpacedRepetitionRecord.
     */
    public SpacedRepetitionRecord initSM_2_forFlashcard(int flashcardId) {
        // Check if a spaced repetition record already exists for the flashcard
        SpacedRepetition spacedRepetition = SPACED_REPETITION;
        var existingRecord = dslContext.selectFrom(spacedRepetition)
                .where(spacedRepetition.SPACED_REPETITION_FLASHCARD_ID.eq(flashcardId))
                .fetchOne();

        if (existingRecord != null) {
            throw new IllegalArgumentException("Spaced repetition record already exists for Flashcard ID: " + flashcardId);
        }

        // Create a new spaced repetition record
        SpacedRepetitionRecord smRecord = dslContext.newRecord(spacedRepetition);
        smRecord.setSpacedRepetitionFlashcardId(flashcardId);
        smRecord.setSpacedRepetitionName(SpacedRepetitionSpacedRepetitionName.SM_2);
        smRecord.setSpacedRepetitionCount(0); // Initial count
        smRecord.setSpacedRepetitionEasinessFactor(2.5); // Default EF (SuperMemo 2 default)
        smRecord.setSpacedRepetitionInterval((double) 0); // Initial interval
        smRecord.setSpacedRepetitionNextDay(LocalDate.now()); // Next day review
        // Insert the new record into the database
        smRecord.store();

        return smRecord;
    }

    // * this will demonstrate how the SM 2 algorithm works
    public SpacedRepetitionRecord triggerSM_2_algorithm(SpacedRepetitionRecord spacedRepetitionRecord, int grade) {
        // Get current values
        double easinessFactor = spacedRepetitionRecord.getSpacedRepetitionEasinessFactor();
        int count = spacedRepetitionRecord.getSpacedRepetitionCount();
        double interval = spacedRepetitionRecord.getSpacedRepetitionInterval();
        LocalDate nextDay = spacedRepetitionRecord.getSpacedRepetitionNextDay();

        if (grade >= 3) {
            // Update the interval and next review date
            if (count == 1) {
                interval = 1; // For the first review, the interval is 1 day
            } else if (count == 2) {
                interval = 6; // After the second review, the interval is 6 days
            } else {
                interval = interval * easinessFactor; // Update the interval with the easiness factor
            }
        } else {
            interval = 1;

        }


        // Adjust easiness factor based on the grade (0 to 5)
        easinessFactor = easinessFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
        if (easinessFactor < 1.3) {
            easinessFactor = 1.3; // Enforce a minimum easiness factor
        }

        // Update next review date
        nextDay = LocalDate.now().plusDays((long) interval);
        // Store the updated values in the record
        spacedRepetitionRecord.setSpacedRepetitionCount(count + 1);
        spacedRepetitionRecord.setSpacedRepetitionEasinessFactor(easinessFactor);
        spacedRepetitionRecord.setSpacedRepetitionInterval(interval);
        spacedRepetitionRecord.setSpacedRepetitionNextDay(nextDay);

        return spacedRepetitionRecord;
    }

    public Optional<SpacedRepetitionRecord> getSpacedRepetitionRecordBelongToFlashcardId(Integer flashcardId) {
        return Optional.ofNullable(
                dslContext.selectFrom(SPACED_REPETITION)
                        .where(SPACED_REPETITION.SPACED_REPETITION_FLASHCARD_ID.eq(flashcardId))
                        .fetchOne()
        );
    }
}
