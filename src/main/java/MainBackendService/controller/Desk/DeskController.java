package MainBackendService.controller.Desk;


import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.DeskDto;
import MainBackendService.dto.HttpErrorDto;
import MainBackendService.dto.SuccessReponseDto;
import MainBackendService.dto.createDto.CreateDeskDto;
import MainBackendService.dto.createDto.CreateFlashcardDto;
import MainBackendService.dto.createDto.CreateFlashcardsDto;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.UserService.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.jooq.sample.model.tables.records.UserRecord;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping(path = "${apiPrefix}/desk")
public class DeskController {
    Logger logger = LogManager.getLogger(DeskController.class);

    @Autowired
    private DeskService deskService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create a new desk", description = "this api use to create a new desk from scratch, you have to login to gain this api")
    public ResponseEntity<?> createDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody CreateDeskDto createDeskDTO) throws BadRequestException {
        // Fetch user by email (from AccessTokenDetailsDto)
        Optional<UserRecord> existUser = userService.findUserByEmail(accessTokenDetailsDto.getEmail());
        if (existUser.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        logger.debug(createDeskDTO.getDeskIcon());

        // * add user id to desk
        createDeskDTO.setDeskOwnerId(existUser.get().getUserId());

        // Create new desk
        DeskRecord newDesk = deskService.createDesk(createDeskDTO);

        logger.debug(newDesk.getCreatedAt());

        // Convert Desk entity to DeskDto
        DeskDto deskDto = new DeskDto(
                String.valueOf(newDesk.getDeskId()), // Assuming deskId is Integer
                newDesk.getDeskName(),
                newDesk.getDeskDescription(),
                newDesk.getDeskThumbnail(),
                newDesk.getDeskIcon(),
                newDesk.getDeskIsPublic() != null && newDesk.getDeskIsPublic() == 1 // Convert Byte to Boolean
        );

        // Return success response
        return new ResponseEntity<>(new SuccessReponseDto(deskDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{desk_id}")
    @Operation(summary = "update desk information")
    public ResponseEntity<?> updateDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId, @Valid @RequestBody DeskDto deskDto) throws JsonProcessingException {
        // Check if the desk exists
        Optional<DeskRecord> desk = deskService.findDeskById(Math.toIntExact(deskId));
        if (desk.isEmpty()) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "Desk not found"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        if (!desk.get().getDeskOwnerId().equals(accessTokenDetailsDto.getId())) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "You are not allow to modify this desk"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        DeskRecord updatedDesk = deskService.updateDesk(Math.toIntExact(deskId), deskDto);
        return new ResponseEntity<>(new SuccessReponseDto<DeskDto>(deskService.getDeskDto(updatedDesk)), HttpStatus.CREATED);


    }

    @DeleteMapping("/{desk_id}")
    @Operation(summary = "delete desk ")
    public ResponseEntity<?> deleteDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId) {
        Optional<DeskRecord> desk = deskService.findDeskById(Math.toIntExact(deskId));
        if (desk.isEmpty()) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "Desk not found"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        if (!desk.get().getDeskOwnerId().equals(accessTokenDetailsDto.getId())) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "You are not allow to modify this desk"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        }
        // Proceed to delete
        deskService.deleteDesk(Math.toIntExact(deskId));
        // Return success response
        return new ResponseEntity<>(new SuccessReponseDto<>("Desk deleted successfully."), HttpStatus.OK);
    }

    @PostMapping("/{desk_id}/flashcard")
    @Operation(summary = "this api create flashcard in desk one per times")
    public ResponseEntity<?> createFlashcard(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId, @Valid @RequestBody CreateFlashcardDto createFlashcardDto) {
        try {
            Optional<DeskRecord> desk = deskService.findDeskById(Math.toIntExact(deskId));
            if (desk.isEmpty()) {
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Desk not found"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            if (!desk.get().getDeskOwnerId().equals(accessTokenDetailsDto.getId())) {
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "You are not allow to modify this desk"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }

            flashcardService.createFlashcardInDesk((Math.toIntExact(deskId)), createFlashcardDto);
            return new ResponseEntity<>(new SuccessReponseDto(HttpStatus.CREATED), HttpStatus.CREATED);

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

    @PostMapping("/{desk_id}/flashcards")
    @Operation(summary = "this api create a bunch flashcards in a desk")
    public ResponseEntity<?> createFlashcards(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId, @Valid @RequestBody CreateFlashcardsDto createFlashcardsDto) {
        try {
            Optional<DeskRecord> desk = deskService.findDeskById(Math.toIntExact(deskId));
            if (desk.isEmpty()) {
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Desk not found"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            if (!desk.get().getDeskOwnerId().equals(accessTokenDetailsDto.getId())) {
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "You are not allow to modify this desk"
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }

            // Get the array of flashcards
            CreateFlashcardDto[] flashcards = createFlashcardsDto.getFlashcards();

            // Check if the array is null or empty
            if (flashcards == null || flashcards.length == 0) {
                throw new IllegalArgumentException("No flashcards provided.");
            }

            // Loop through each flashcard
            for (CreateFlashcardDto flashcard : flashcards) {
                flashcardService.createFlashcardInDesk(Math.toIntExact(deskId), flashcard);
            }

            return new ResponseEntity<>(new SuccessReponseDto(HttpStatus.CREATED), HttpStatus.CREATED);

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

    @PostMapping("/clone/{desk_id}")
    @Operation(summary = "this api use to clone a desk from a user to another user", description = "this api will copy the desk information, including the flashcards information, ")
    public ResponseEntity<?> cloneDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId) throws HttpResponseException {
        deskService.cloneDesk(Math.toIntExact(deskId), accessTokenDetailsDto.getId());

        return new ResponseEntity<>(new SuccessReponseDto("Cloned"), HttpStatus.CREATED);

    }
}