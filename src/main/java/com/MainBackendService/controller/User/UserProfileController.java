package com.MainBackendService.controller.User;

import com.MainBackendService.Authentication.AccessTokenFilter.CustomJwtAuth;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${apiPrefix}/user/profile")
public class UserProfileController {
    Logger logger = LogManager.getLogger(UserProfileController.class);
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal CustomJwtAuth userDetails) {

        UserProfileDto profile = userService.getUserProfile(userDetails.getUserEmail());
        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(profile), HttpStatus.OK);

    }

    @PatchMapping("")
    public ResponseEntity<?> patchUserProfile(@AuthenticationPrincipal CustomJwtAuth userDetails, @Valid @RequestBody UserProfilePatchDto userProfilePatchDto) {
        UserProfileDto profile = userService.updateUserProfile(userDetails.getUserEmail(), userProfilePatchDto);

        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(profile), HttpStatus.OK);

    }
}
