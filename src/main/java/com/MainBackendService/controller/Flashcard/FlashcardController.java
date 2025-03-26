package com.MainBackendService.controller.Flashcard;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.SuccessReponseDto;
import com.MainBackendService.exception.HttpNotFoundException;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.exception.HttpUnauthorizedException;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "${apiPrefix}/flashcard")
public class FlashcardController {
    Logger logger = LogManager.getLogger(FlashcardController.class);

    @Autowired
    private SM_2_Service sm_2_service;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private DeskService deskService;

    @PatchMapping("/review/{flashcard_id}/grade/{grade}")
    @Operation(summary = "this api is for review a flashcard", description = "To use this api, user should login to gain access tokens, use that token to have the access to the user's desk that contains the flashcard ")
    public ResponseEntity<?> reviewFlashcard(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                             @PathVariable("flashcard_id") String flashcardId,
                                             @PathVariable("grade") String grade

    ) throws HttpResponseException {
        // * verify if the user have the access to the flashcard's desk
        Optional<FlashcardRecord> flashcardRecord = flashcardService.getFlashcardById(Integer.valueOf(flashcardId));
        if (flashcardRecord.isEmpty()) throw new HttpNotFoundException("flashcard not found");

        if (!deskService.isUserOwnerOfDesk(accessTokenDetailsDto.getId(), flashcardRecord.get().getFlashcardDeskId())) {
            throw new HttpUnauthorizedException("Access denied");
        }

        // * check if the flashcard have already existed the spaced repetition record or not
        SpacedRepetitionRecord spacedRepetitionRecord = sm_2_service.getSpacedRepetitionRecordBelongToFlashcardId(flashcardRecord.get().getFlashcardId());

        // * update the SM 2 object after review the flashcard
        SpacedRepetitionRecord updatedSpacedRepetitionRecord = sm_2_service.triggerSM_2_algorithm(spacedRepetitionRecord, Integer.valueOf(grade));
        return new ResponseEntity<>(new SuccessReponseDto("Review successfully"), HttpStatus.CREATED);
    }
}
