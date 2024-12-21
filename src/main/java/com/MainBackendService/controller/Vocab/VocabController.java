package com.MainBackendService.controller.Vocab;

import com.MainBackendService.controller.User.UserProfileController;
import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.CreateVocabDto;
import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.dto.SuccessReponseDto;
import com.MainBackendService.model.Vocab;
import com.MainBackendService.service.DeskVocabFlashcardService;
import com.MainBackendService.service.VocabService;
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
    private DeskVocabFlashcardService deskVocabFlashcardService;

    @PostMapping()
    public ResponseEntity<?> createNewVocab(
            @PathVariable("desk_id") String deskId,
            @Valid AccessTokenDetailsDto accessTokenDetailsDto,
            @Valid @RequestBody CreateVocabDto createVocabDto
    ) {
        try {
            Vocab newVocab = vocabService.createVocab(createVocabDto);

            deskVocabFlashcardService.createAssociateVocabWithDesk(newVocab.getVocabId(), Integer.valueOf(deskId));

            SuccessReponseDto<Vocab> successReponseDto = new SuccessReponseDto<>(newVocab);

            return new ResponseEntity<>(successReponseDto, HttpStatus.CREATED);

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
