package com.MainBackendService.service;

import com.MainBackendService.dto.SignInDto;
import com.MainBackendService.dto.SignUpDTO;
import com.MainBackendService.dto.UserProfileDto;
import com.MainBackendService.dto.UserProfilePatchDto;
import com.MainBackendService.model.User;
import com.MainBackendService.model.UserToken;
import com.MainBackendService.repository.UserRepository;
import com.MainBackendService.repository.UserTokenRepository;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, UserTokenRepository userTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPasswordWithOtp(String otp, String email, String password) {
        if (verifyOtp(email, otp)) {
            Optional<User> findUser = userRepository.findByUserEmail(email);
            if (findUser.isPresent()) {
                // Encrypt the password
                String encryptedPassword = passwordEncoder.encode(password);
                User existUser = findUser.get();
                existUser.setUserPassword(encryptedPassword);

                userRepository.save(existUser);
            }
        }
    }

    public boolean verifyOtp(String email, String otp) {
        // Find the latest OTP for the user by email
        Page<UserToken> latestTokenOpts = userTokenRepository.findOtpByEmail(email, PageRequest.of(0, 1));
        if (!latestTokenOpts.isEmpty()) {
            logger.debug(latestTokenOpts.getContent().get(0).getUTText());

            UserToken latestToken = latestTokenOpts.getContent().get(0);

            // Check if the OTP matches and the token is not expired
            return latestToken.getUTText().equals(otp) && latestToken.getUTExpiredAt().isAfter(LocalDateTime.now()); // OTP is valid
        }

        return false; // OTP is invalid or expired
    }

    public User signUp(SignUpDTO signUpDTO) {
        // Check if the username already exists
        if (userRepository.existsByUserName(signUpDTO.getUser_name())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByUserEmail(signUpDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");

        }
        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(signUpDTO.getUser_password());
        // Create a new user object
        User newUser = new User();
        newUser.setUserName(signUpDTO.getUser_name());
        newUser.setUserPassword(encryptedPassword);  // Encrypt the password
        newUser.setUserEmail(signUpDTO.getEmail());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdateAt(LocalDateTime.now());

        // Save the user to the database
        return userRepository.save(newUser);
    }

    public User signIn(SignInDto signInDto) throws IOException {
        Optional<User> foundUser = userRepository.findByUserEmail(signInDto.getUser_email());

        User existUser = foundUser.orElseThrow(() -> new RuntimeException("User not found"));

        // Validate password (assume passwords are hashed)
        if (!passwordEncoder.matches(signInDto.getUser_password(), existUser.getUserPassword())) {
            throw new BadRequestException("Invalid password");
        }

        // If the email and password are correct, return the user
        return existUser;
    }

    public UserProfileDto getUserProfile(String email) {
        Optional<User> findUser = userRepository.findByUserEmail(email);
        if (findUser.isEmpty()) {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found with email: " + email);
        }
        User user = findUser.get();
        UserProfileDto profile = new UserProfileDto();
        profile.setId(String.valueOf(user.getUserId())); // Convert Integer to String for id
        profile.setEmail(user.getUserEmail());
        profile.setName(user.getUserName());
        profile.setAvatar(user.getUserAvatar());
        profile.setThumbnail(user.getUserThumbnail());

        return profile;
    }

    public UserProfileDto updateUserProfile(String email, UserProfilePatchDto userProfilePatchDto) {

        Optional<User> findUser = userRepository.findByUserEmail(email);

        if (findUser.isEmpty()) {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found with email: " + email);
        }

        User user = findUser.get();

        user.setUserAvatar(userProfilePatchDto.getAvatar());
        user.setUserThumbnail(userProfilePatchDto.getThumbnail());
        user.setUserName(userProfilePatchDto.getName());

        userRepository.save(user);

        UserProfileDto profile = new UserProfileDto();
        profile.setId(String.valueOf(user.getUserId())); // Convert Integer to String for id
        profile.setEmail(user.getUserEmail());
        profile.setName(user.getUserName());
        profile.setAvatar(user.getUserAvatar());
        profile.setThumbnail(user.getUserThumbnail());

        return profile;

    }
}
