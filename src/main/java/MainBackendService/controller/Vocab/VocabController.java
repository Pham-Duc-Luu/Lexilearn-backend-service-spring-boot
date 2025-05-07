package MainBackendService.controller.Vocab;

import MainBackendService.controller.User.UserProfileController;
import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.HttpErrorDto;
import MainBackendService.dto.SuccessReponseDto;
import MainBackendService.dto.createDto.CreateVocabDto;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import MainBackendService.service.VocabService.VocabService;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import com.jooq.sample.model.tables.records.VocabRecord;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${apiPrefix}/desk/{desk_id}/vocab")
public class VocabController {
    Logger logger = LogManager.getLogger(UserProfileController.class);

    @Autowired
    private VocabService vocabService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private SM_2_Service sm_2_service;

    @PostMapping()
    public ResponseEntity<?> createNewVocab(
            @PathVariable("desk_id") String deskId,
            @Valid AccessTokenDetailsDto accessTokenDetailsDto,
            @Valid @RequestBody CreateVocabDto createVocabDto
    ) {
        try {
            // * create a new vocabulary
            VocabRecord vocabRecord = vocabService.createVocab(Integer.valueOf(deskId), createVocabDto);

            // * create a new flashcard with that vocabulary too
            FlashcardRecord flashcardRecord = flashcardService.initFlashcardForVocabularyInDesk(vocabRecord.getVocabId());

            // * attach the spaced repetition to flashcard ( default is sm-0 )
            SpacedRepetitionRecord spacedRepetitionRecord = sm_2_service.initSM_2_forFlashcard(flashcardRecord.getFlashcardId());
            return new ResponseEntity<>(new SuccessReponseDto("create new vocabulary successfully"), HttpStatus.CREATED);

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
