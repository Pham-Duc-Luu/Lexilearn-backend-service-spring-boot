package com.MainBackendService.controller.User;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.SuccessReponseDto;
import com.MainBackendService.dto.UserProfileDto;
import com.MainBackendService.dto.UserProfilePatchDto;
import com.MainBackendService.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "${apiPrefix}/user/profile")
public class UserProfileController {
    Logger logger = LogManager.getLogger(UserProfileController.class);
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserProfile(@Valid AccessTokenDetailsDto accessTokenDetailsDto) {

        Optional<UserProfileDto> userProfileDto = userService.getUserProfile(accessTokenDetailsDto.getEmail());


        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(userProfileDto.get()), HttpStatus.OK);

    }

    @PatchMapping()
    public ResponseEntity<?> updateUserProfile(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody UserProfilePatchDto userProfilePatchDto) throws Exception {


        UserProfileDto userProfileDto = userService.updateUserProfile(accessTokenDetailsDto.getEmail(), accessTokenDetailsDto.getName(), userProfilePatchDto);
        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(userProfileDto), HttpStatus.OK);
    }
}
