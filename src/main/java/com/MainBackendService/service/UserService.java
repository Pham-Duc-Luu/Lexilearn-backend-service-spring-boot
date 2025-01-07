package com.MainBackendService.service;

import com.MainBackendService.dto.SignInDto;
import com.MainBackendService.dto.SignUpDTO;
import com.MainBackendService.dto.UserProfileDto;
import com.MainBackendService.dto.UserProfilePatchDto;
import com.MainBackendService.modal.UserModal;
import com.jooq.sample.model.enums.UserTokenUtType;
import com.jooq.sample.model.enums.UserUserProvider;
import com.jooq.sample.model.tables.records.UserRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.jooq.sample.model.tables.User.USER;
import static com.jooq.sample.model.tables.UserToken.USER_TOKEN;

@Service
public class UserService {

    private final DSLContext dslContext;

    Logger logger = LogManager.getLogger(UserService.class);
    BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(10);

    @Autowired
    public UserService(DSLContext dslContext) {

        this.dslContext = dslContext;
    }


    public void resetPasswordWithOtp(String otp, String email, String password) {
        if (verifyOtp(email, otp)) {
            // Hash the new password
            String hashedPassword = passwordEncoder.encode(password);

            // Update the user's password
            int rowsUpdated = dslContext.update(USER)
                    .set(USER.USER_PASSWORD, hashedPassword)
                    .where(USER.USER_EMAIL.eq(email))
                    .execute();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("Failed to update password. User not found.");
            }

        }
    }

    public boolean verifyOtp(String email, String otp) {
        // Find the latest OTP for the user by email
        var tokenRecord = dslContext.selectFrom(USER_TOKEN)
                .where(USER_TOKEN.UT_USER_ID.in(
                        dslContext.select(USER.USER_ID)
                                .from(USER)
                                .where(USER.USER_EMAIL.eq(email))
                ))
                .and(USER_TOKEN.UT_TYPE.eq(UserTokenUtType.OTP))
                .orderBy(USER_TOKEN.UT_EXPIRED_AT.desc())
                .fetchOne();

        // Check if a record was found
        if (tokenRecord == null) {
            return false; // No OTP found for this email
        }

        // Verify if the OTP matches and is not expired
        boolean isValidOtp = tokenRecord.getUtText().equals(otp) &&
                tokenRecord.getUtExpiredAt().isAfter(LocalDateTime.now());

        return isValidOtp;
    }

    public UserRecord signUp(SignUpDTO signUpDTO) {

        // Step 1: Check if the email already exists in the database using jOOQ
        boolean emailExists = dslContext.fetchCount(dslContext.selectFrom(USER)
                .where(USER.USER_EMAIL.eq(signUpDTO.getUser_email()))) > 0;

        if (emailExists) {
            throw new IllegalArgumentException("Email is already taken");
        }

        // Step 2: Encrypt the password
        String encryptedPassword = passwordEncoder.encode(signUpDTO.getUser_password());

        // Step 3: Insert the new user into the database using jOOQ
        int inserted = dslContext.insertInto(USER)
                .set(USER.USER_NAME, signUpDTO.getUser_name())
                .set(USER.USER_PASSWORD, encryptedPassword)
                .set(USER.USER_EMAIL, signUpDTO.getUser_email())
                .set(USER.CREATED_AT, LocalDateTime.now())
                .set(USER.UPDATE_AT, LocalDateTime.now())
                .set(USER.USER_PROVIDER, signUpDTO.getUserAuthProvider() != null ? signUpDTO.getUserAuthProvider() : UserUserProvider.LOCAL)
                .set(USER.USER_AVATAR, signUpDTO.getUser_avatar()) // Optional avatar
                .set(USER.USER_THUMBNAIL, signUpDTO.getUser_thumbnail()) // Optional thumbnail
                .execute();

        // Step 4: Return the created user record (or fetch it from the database)
        if (inserted > 0) {
            // Fetch the user record after insertion
            return dslContext.selectFrom(USER)
                    .where(USER.USER_EMAIL.eq(signUpDTO.getUser_email()))
                    .fetchOne();
        } else {
            throw new RuntimeException("Failed to sign up user");
        }
    }

    public UserRecord signIn(SignInDto signInDto) throws IOException {
        // Fetch the user by email
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_EMAIL.eq(signInDto.getUser_email()))
                .fetchOne();

        if (userRecord == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Verify the password
        boolean isPasswordValid = passwordEncoder.matches(signInDto.getUser_password(), userRecord.getUserPassword());
        if (!isPasswordValid) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Return the user record
        return userRecord;
    }

    public Optional<UserProfileDto> getUserProfile(String email) {
        return Optional.ofNullable(
                dslContext.selectFrom(USER)
                        .where(USER.USER_EMAIL.eq(email))
                        .fetchOne()
        ).map(userRecord -> {
            UserProfileDto dto = new UserProfileDto();
            dto.setId(String.valueOf(userRecord.getUserId()));
            dto.setEmail(userRecord.getUserEmail());
            dto.setName(userRecord.getUserName());
            dto.setAvatar(userRecord.getUserAvatar());
            dto.setThumbnail(userRecord.getUserThumbnail());
            return dto;
        });
    }


    public UserProfileDto updateUserProfile(String email, String name, UserProfilePatchDto userProfilePatchDto) {
        // Fetch the user record by email
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_EMAIL.eq(email))
                .fetchOne();

        if (userRecord == null) {
            throw new IllegalArgumentException("User not found with the given email: " + email);
        }

        // Update the fields based on UserProfilePatchDto
        if (userProfilePatchDto.getName() != null && !userProfilePatchDto.getName().isEmpty()) {
            userRecord.setUserName(userProfilePatchDto.getName());
        }

        if (userProfilePatchDto.getAvatar() != null) {
            userRecord.setUserAvatar(userProfilePatchDto.getAvatar());
        }

        if (userProfilePatchDto.getThumbnail() != null) {
            userRecord.setUserThumbnail(userProfilePatchDto.getThumbnail());
        }

        // Update the user in the database
        userRecord.store();

        // Map updated UserRecord to UserProfileDto
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setId(String.valueOf(userRecord.getUserId()));
        updatedProfile.setEmail(userRecord.getUserEmail());
        updatedProfile.setName(userRecord.getUserName());
        updatedProfile.setAvatar(userRecord.getUserAvatar());
        updatedProfile.setThumbnail(userRecord.getUserThumbnail());

        return updatedProfile;
    }


    public Optional<UserRecord> findUserByEmail(String email) {
        // Fetch the user record by email
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_EMAIL.eq(email))
                .fetchOne();

        return Optional.ofNullable(userRecord);
    }


    public UserModal findUserById(Integer userId) {
        // Fetch user from the database by user ID
        return dslContext.selectFrom(USER)
                .where(USER.USER_ID.eq(userId))
                .fetchOneInto(UserModal.class);
    }

}
