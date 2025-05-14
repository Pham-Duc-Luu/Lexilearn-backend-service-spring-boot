package MainBackendService.controller.User;

import MainBackendService.Microservice.ImageServerService.dto.ImageDto;
import MainBackendService.Microservice.ImageServerService.service.ImageService;
import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.SuccessReponseDto;
import MainBackendService.dto.UserProfileDto;
import MainBackendService.dto.UserProfilePatchDto;
import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.service.UserService.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping(path = "${apiPrefix}/user/profile")
public class UserProfileController {
    Logger logger = LogManager.getLogger(UserProfileController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("")
    public ResponseEntity<?> getUserProfile(@Valid AccessTokenDetailsDto accessTokenDetailsDto) {
        Optional<UserProfileDto> userProfileDto = userService.getUserProfile(accessTokenDetailsDto.getEmail());
        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(userProfileDto.get()), HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<SuccessReponseDto<UserProfileDto>> updateUserProfile(@Valid AccessTokenDetailsDto accessTokenDetailsDto, @Valid @RequestBody UserProfilePatchDto userProfilePatchDto) throws Exception {

        UserProfileDto userProfileDto = userService.updateUserProfile(accessTokenDetailsDto.getEmail(), accessTokenDetailsDto.getName(), userProfilePatchDto);
        return new ResponseEntity<>(new SuccessReponseDto<UserProfileDto>(userProfileDto), HttpStatus.OK);
    }


    @PostMapping("/avatar")
    public ResponseEntity<SuccessReponseDto<ImageDto>> uploadUserAvatar(@Valid AccessTokenDetailsDto accessTokenDetailsDto,
                                                                        @RequestParam("image") MultipartFile file,
                                                                        @RequestHeader(value = "Authorization", required = true) String authorizationHeader) throws HttpBadRequestException {

        ImageDto imageDto = imageService.uploadImage(file, authorizationHeader);
        String avatarUrl = imageDto.getUrl();
        userService.updateUserAvatarUrl(accessTokenDetailsDto.getId(), avatarUrl);
        return new ResponseEntity<>(new SuccessReponseDto(imageDto), HttpStatus.CREATED);

    }

}
