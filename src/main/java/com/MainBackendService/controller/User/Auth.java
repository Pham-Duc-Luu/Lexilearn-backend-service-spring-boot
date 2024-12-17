package com.MainBackendService.controller.User;

import com.MainBackendService.controller.hello;
import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.dto.JwtAuthDto;
import com.MainBackendService.dto.SignUpDTO;
import com.MainBackendService.model.User;
import com.MainBackendService.model.UserToken;
import com.MainBackendService.service.TokenService;
import com.MainBackendService.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "${apiPrefix}/users/auth")
@Validated
public class Auth {
    Logger logger = LogManager.getLogger(Auth.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;




    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp( @RequestBody  @Valid  SignUpDTO signUpDTO, BindingResult result) {
        try {
            if (result.hasErrors()){
                List<String> errorMessages = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

                return ResponseEntity.badRequest().body(errorMessages);
            }

            // Call service to create a new user
            User newUser = userService.signUp(signUpDTO);

            UserToken userToken = tokenService.saveRefreshToken(newUser);

            // Return success response
            return new ResponseEntity<JwtAuthDto>( new JwtAuthDto(userToken.getUTText(), tokenService.saveAccessToken(newUser)), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            HttpErrorDto errorResponse = new HttpErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    e.getMessage()

            );
            // Return error response
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
