package com.MainBackendService.controller.Desk;


import com.MainBackendService.controller.Auth.Auth;
import com.MainBackendService.dto.*;
import com.MainBackendService.model.Desk;
import com.MainBackendService.model.User;
import com.MainBackendService.service.DeskService;
import com.MainBackendService.service.UserService;
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
    Logger logger = LogManager.getLogger(Auth.class);

    @Autowired
    private DeskService deskService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody CreateDeskDto createDeskDTO) throws BadRequestException {
        // Fetch user by email (from AccessTokenDetailsDto)
        Optional<User> existUser = userService.findUserByEmail(accessTokenDetailsDto.getEmail());
        if (existUser.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        // * add user id to desk
        createDeskDTO.setDeskOwnerId(existUser.get().getUserId());

        // Create new desk
        Desk newDesk = deskService.createDesk(createDeskDTO);

        // Convert Desk entity to DeskDto
        DeskDto deskDto = new DeskDto(
                String.valueOf(newDesk.getDeskId()), // Assuming deskId is Integer
                newDesk.getDeskName(),
                newDesk.getDeskDescription(),
                newDesk.getDeskThumbnail(),
                newDesk.getDeskIcon(),
                newDesk.getDeskIsPublic()
        );

        // Return success response
        return new ResponseEntity<>(new SuccessReponseDto(deskDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{desk_id}")
    public ResponseEntity<?> updateDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId, @Valid @RequestBody DeskDto deskDto) {
        // Check if the desk exists
        Optional<Desk> desk = deskService.findDeskById(Math.toIntExact(deskId));
        if (desk.isEmpty()) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "Desk not found"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        if (!desk.get().getDeskOwner().getUserId().equals(accessTokenDetailsDto.getId())) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "You are not allow to modify this desk"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        Desk updatedDesk = deskService.updateDesk(Math.toIntExact(deskId), deskDto);
        return new ResponseEntity<>(new SuccessReponseDto<DeskDto>(deskService.getDeskDto(updatedDesk)), HttpStatus.CREATED);


    }

    @DeleteMapping("/{desk_id}")
    public ResponseEntity<?> deleteDesk(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @PathVariable("desk_id") Long deskId) {
        Optional<Desk> desk = deskService.findDeskById(Math.toIntExact(deskId));
        if (desk.isEmpty()) {
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    "Desk not found"
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        if (!desk.get().getDeskOwner().getUserId().equals(accessTokenDetailsDto.getId())) {
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