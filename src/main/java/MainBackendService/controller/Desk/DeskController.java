package MainBackendService.controller.Desk;


import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.Desk.CreateDeskDto;
import MainBackendService.dto.Desk.UpdateDeskDto;
import MainBackendService.dto.DeskDto;
import MainBackendService.dto.HttpErrorDto;
import MainBackendService.dto.SuccessReponseDto;
import MainBackendService.exception.HttpForbiddenException;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.modal.DeskModal;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.UserService.UserService;
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
    public ResponseEntity<SuccessReponseDto<DeskModal>> createDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody CreateDeskDto createDeskDTO) throws BadRequestException {
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

    @PatchMapping("")
    @Operation(summary = "update desk information, using patch -> update a part of the desk, if the input field = null -> not update")
    // TODO : change the deskdto -> need more clearly
    public ResponseEntity<SuccessReponseDto<DeskModal>> patchUpdateDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody UpdateDeskDto updateDeskDto) throws HttpResponseException {

        DeskRecord deskRecord = deskService.getDeskById(updateDeskDto.getDesk_id());
        if (deskRecord == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        if (!deskService.isUserOwnerOfDesk(accessTokenDetailsDto.getId(), deskRecord.getDeskId()))
            throw new HttpForbiddenException("You are not allow to modify this resource");


        deskRecord = deskService.updateAPartDesk(updateDeskDto.getDesk_id(), updateDeskDto);

        return new ResponseEntity<SuccessReponseDto<DeskModal>>(new SuccessReponseDto<DeskModal>(new DeskModal(deskRecord)), HttpStatus.OK);

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

}