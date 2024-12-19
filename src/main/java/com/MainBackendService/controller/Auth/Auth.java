package com.MainBackendService.controller.Auth;

import com.MainBackendService.dto.*;
import com.MainBackendService.model.User;
import com.MainBackendService.service.EmailService;
import com.MainBackendService.service.TokenService;
import com.MainBackendService.service.UserService;
import com.MainBackendService.utils.Otp;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "${apiPrefix}/auth")
@Validated
public class Auth {
    Logger logger = LogManager.getLogger(Auth.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDTO signUpDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

                return ResponseEntity.badRequest().body(errorMessages);
            }

            // Call service to create a new user
            User newUser = userService.signUp(signUpDTO);

            // * set access and refresh token
            String accessToken = tokenService.setAccessToken(newUser);
            String refreshToken = tokenService.setRefreshToken(newUser);

            // Return success response
            return new ResponseEntity<JwtAuthDto>(new JwtAuthDto(refreshToken, accessToken), HttpStatus.CREATED);
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

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInDto signInDto) {
        try {

            User existUser = userService.signIn(signInDto);

            // * set access and refresh token
            String accessToken = tokenService.setAccessToken(existUser);
            String refreshToken = tokenService.setRefreshToken(existUser);

            // Return success response
            return new ResponseEntity<JwtAuthDto>(new JwtAuthDto(refreshToken, accessToken), HttpStatus.CREATED);

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

    @PostMapping("/reset-password/send-otp-to-email")
    public ResponseEntity<?> SendOtpToEmail(@Valid @RequestBody SendOtpToEmailDto sendOtpToEmailDto) {
        try {
            String opt = Otp.generateOtp();

            emailService.sendOtpEmail(sendOtpToEmailDto.getEmail(), opt);

            tokenService.setOtp(sendOtpToEmailDto.getEmail(), opt);
            return new ResponseEntity<>("OTP send!", HttpStatus.OK);

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

    @PostMapping("/reset-password/verify-otp-and-change-password")
    public ResponseEntity<?> verifyAndChangePassword(@Valid @RequestBody VerifyOtpAndChangePasswordDto verifyOtpAndChangePasswordDto) {
        try {
            String opt = Otp.generateOtp();
            if (!userService.verifyOtp(verifyOtpAndChangePasswordDto.getEmail(), verifyOtpAndChangePasswordDto.getOtp())) {
                HttpErrorDto errorResponse = new HttpErrorDto(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "OTP expired!"
                );
                // Return error response
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            userService.resetPasswordWithOtp(verifyOtpAndChangePasswordDto.getOtp(), verifyOtpAndChangePasswordDto.getEmail(), verifyOtpAndChangePasswordDto.getPassword());

            return new ResponseEntity<>("Password changed", HttpStatus.OK);

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
