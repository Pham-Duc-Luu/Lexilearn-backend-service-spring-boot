package com.MainBackendService.controller.Desk;


import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.DeskDto;
import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.dto.SuccessReponseDto;
import com.MainBackendService.dto.createDto.CreateDeskDto;
import com.MainBackendService.dto.createDto.CreateFlashcardDto;
import com.MainBackendService.dto.createDto.CreateFlashcardsDto;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.UserService;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.jooq.sample.model.tables.records.UserRecord;
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
    public ResponseEntity<?> createDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody CreateDeskDto createDeskDTO) throws BadRequestException {
        // Fetch user by email (from AccessTokenDetailsDto)
        Optional<UserRecord> existUser = userService.findUserByEmail(accessTokenDetailsDto.getEmail());
        if (existUser.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        // * add user id to desk
        createDeskDTO.setDeskOwnerId(existUser.get().getUserId());

        // Create new desk
        DeskRecord newDesk = deskService.createDesk(createDeskDTO);

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
    public ResponseEntity<?> updateDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId, @Valid @RequestBody DeskDto deskDto) {
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


}