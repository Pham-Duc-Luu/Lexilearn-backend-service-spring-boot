package com.MainBackendService.controller.Flashcard;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.dto.SuccessReponseDto;
import com.MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
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

@RestController
@RequestMapping(path = "${apiPrefix}/flashcard")
public class FlashcardController {
    Logger logger = LogManager.getLogger(FlashcardController.class);

    @Autowired
    private SM_2_Service sm_2_service;

    @PatchMapping("/review/{flashcard_id}/grade/{grade}")
    public ResponseEntity<?> reviewFlashcard(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                             @PathVariable("flashcard_id") String flashcardId,
                                             @PathVariable("grade") String grade

    ) {
        try {

            // * reset the SM 2 object after review the flashcard
            SpacedRepetitionRecord spacedRepetitionRecord = sm_2_service.triggerSM_2_algorithm(Integer.valueOf(flashcardId), Integer.valueOf(grade));
            return new ResponseEntity<>(new SuccessReponseDto("Review successfully"), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            HttpErrorDto httpErrorDto = new HttpErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    e.getMessage(), "");
            return new ResponseEntity<>(httpErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


}
