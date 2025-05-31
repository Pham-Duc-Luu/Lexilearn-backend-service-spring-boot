package MainBackendService.controller.Flashcard;

import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.Flashcard.*;
import MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import MainBackendService.dto.SuccessReponseDto;
import MainBackendService.exception.*;
import MainBackendService.modal.FlashcardModal;
import MainBackendService.service.DeskService.DeskFlashcardsLinkedListOperation;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_Service;
import MainBackendService.service.UserService.UserService;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.jooq.sample.model.tables.records.FlashcardRecord;
import com.jooq.sample.model.tables.records.SpacedRepetitionRecord;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "${apiPrefix}/flashcard")
public class FlashcardController {
    Logger logger = LogManager.getLogger(FlashcardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private DeskFlashcardsLinkedListOperation deskFlashcardsLinkedListOperation;

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

    @PostMapping("/upsert/list")
    @Operation(
            summary = "Upsert multiple flashcards into a desk",
            description = "This endpoint allows the creation, updating, or deletion of multiple flashcards in a single operation. "
                    + "All flashcards must belong to the same desk. "
                    + "Each flashcard must have a valid operation type (CREATE, UPDATE, DELETE), "
                    + "and positions must be unique within the list."
    )
    public ResponseEntity<SuccessReponseDto> upsertFlashcards(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                                              @Valid @RequestBody UpsertFlashcardRequestBodyDto upsertFlashcardRequestBodyDto
    ) throws HttpResponseException {

        if (deskService.getDeskById(upsertFlashcardRequestBodyDto.getDeskId()) == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        if (!deskService.isUserOwnerOfDesk(accessTokenDetailsDto.getId(), upsertFlashcardRequestBodyDto.getDeskId()))
            throw new HttpForbiddenException("You are not allow to modify this desk");

        if (!upsertFlashcardRequestBodyDto.hasUniquePositions())
            throw new HttpBadRequestException("Your flashcard's position is not unique");
        if (!upsertFlashcardRequestBodyDto.hasRightOperationType())
            throw new HttpBadRequestException("Your flashcard's operation type is wrong");

        // * check if all the item is right operation
        for (UpsertFlashcardDto upsertFlashcardDto : upsertFlashcardRequestBodyDto.getData()) {
            if (!upsertFlashcardDto.isValidOperation())
                throw new HttpBadRequestException("Item's operation type is wrong");

        }


        return new ResponseEntity<>(new SuccessReponseDto("Upsert successfully"), HttpStatus.CREATED);
    }

    @PostMapping
    @Operation()
    public ResponseEntity<SuccessReponseDto> insertNewFlashcard(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                                                @Valid @RequestBody InsertFlashcardRequestBodyDto insertFlashcardRequestBodyDto

    ) throws HttpResponseException {
        Integer deskId = insertFlashcardRequestBodyDto.getDeskId();

        InsertFlashcardDto data = insertFlashcardRequestBodyDto.getData();


        if (deskService.getDeskById(deskId) == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        if (!deskService.isUserOwnerOfDesk(accessTokenDetailsDto.getId(), deskId))
            throw new HttpForbiddenException("You are not allow to modify this desk");

        if (!data.getOperation().equals(OperationType.CREATE))
            throw new HttpBadRequestException("Your flashcard's operation type is wrong");


        // * get the next item of the previous card and apply to this new card
        FlashcardModal newFlashcard = data.mapToFlashcardModal();
        FlashcardRecord newFlashcardRecord = flashcardService.createFlashcard(deskId, newFlashcard);

        deskFlashcardsLinkedListOperation.insertFlashcardOperation(newFlashcardRecord.getFlashcardId()).atTail();

        return new ResponseEntity<>(new SuccessReponseDto("created"), HttpStatus.CREATED);


    }


    @GetMapping("/linked-list")
    public ResponseEntity<FlashcardPaginationResult> getLinkedListFlashcard(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                                                            @RequestParam("desk_id") Integer deskId,
                                                                            @RequestParam(value = "skip", required = false, defaultValue = "0") Integer skip,
                                                                            @RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit

    ) throws HttpResponseException {

        DeskRecord deskRecord = deskService.getDeskById(deskId);

        if (deskRecord == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        List<Integer> flashcardIdList = deskFlashcardsLinkedListOperation.linkedListTraverse(deskRecord);
        int start = Math.min(skip, flashcardIdList.size());
        int end = Math.min(start + limit, flashcardIdList.size());

        List<FlashcardModal> flashcardModalList = new ArrayList<>();

        for (Integer flashcardId : flashcardIdList) {
            try {
                flashcardModalList.add(flashcardService.getFlashcard(flashcardId));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
                break;
            }
        }

     
        return new ResponseEntity(new FlashcardPaginationResult(flashcardModalList, flashcardIdList.size(), skip, limit
        ), HttpStatus.OK);

    }
}
