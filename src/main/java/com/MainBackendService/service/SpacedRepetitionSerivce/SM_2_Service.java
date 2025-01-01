package com.MainBackendService.service.SpacedRepetitionSerivce;

import com.jooq.sample.model.enums.SpacedRepetitionSpacedRepetitionName;
import com.jooq.sample.model.tables.SpacedRepetition;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        SpacedRepetition spacedRepetition = SpacedRepetition.SPACED_REPETITION;
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

    public SpacedRepetitionRecord triggerSM_2_algorithm(Integer SpacedRepetitionId, int grade) {
        // Retrieve the spaced repetition record for the given ID
        SpacedRepetition spacedRepetition = SpacedRepetition.SPACED_REPETITION;
        var record = dslContext.selectFrom(spacedRepetition)
                .where(spacedRepetition.SPACED_REPETITION_ID.eq(SpacedRepetitionId))
                .fetchOne();


        if (record == null) {
            throw new IllegalArgumentException("No spaced repetition record found for ID: " + SpacedRepetitionId);
        }

        // Get current values
        double easinessFactor = record.getSpacedRepetitionEasinessFactor();
        int count = record.getSpacedRepetitionCount();
        double interval = record.getSpacedRepetitionInterval();
        LocalDate nextDay = record.getSpacedRepetitionNextDay();

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
        record.setSpacedRepetitionCount(count + 1);
        record.setSpacedRepetitionEasinessFactor(easinessFactor);
        record.setSpacedRepetitionInterval(interval);
        record.setSpacedRepetitionNextDay(nextDay);

        // Save the updated record back to the database
        record.store();

        return record;
    }
}
