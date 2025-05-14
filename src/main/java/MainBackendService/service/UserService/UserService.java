package MainBackendService.service.UserService;

import MainBackendService.dto.GraphqlDto.AvatarDto;
import MainBackendService.dto.GraphqlDto.ModifyUserProfileInput;
import MainBackendService.dto.SignInDto;
import MainBackendService.dto.SignUpDTO;
import MainBackendService.dto.UserProfileDto;
import MainBackendService.dto.UserProfilePatchDto;
import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.modal.UserModal;
import com.jooq.sample.model.enums.UserTokenUtType;
import com.jooq.sample.model.enums.UserUserProvider;
import com.jooq.sample.model.tables.records.UserRecord;
import com.jooq.sample.model.tables.records.UserTokenRecord;
import com.jooq.sample.model.tables.records.UseravatarRecord;
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
import static com.jooq.sample.model.tables.Useravatar.USERAVATAR;

@Service
public class UserService {

    @Autowired
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
        Optional<UserRecord> userRecord = findUserByEmail(email);

        if (userRecord.isEmpty()) {
            return false;
        }
        // Find the latest OTP for the user by email
        UserTokenRecord tokenRecord = dslContext.selectFrom(USER_TOKEN)
                .where(USER_TOKEN.UT_TEXT.eq(otp)
                        .and(USER_TOKEN.UT_USER_ID.eq(userRecord.get().getUserId())
                                .and(USER_TOKEN.UT_TYPE.eq(UserTokenUtType.OTP))
                        ))
                .fetchOne();

        // Check if a record was found
        if (tokenRecord == null) {
            return false; // No OTP found for this email
        }
        // Verify if the OTP matches and is not expired
        boolean isValidOtp = tokenRecord.getUtExpiredAt().isAfter(LocalDateTime.now());
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

    public UserProfileDto updateUserProfile(Integer userId, ModifyUserProfileInput modifyUserProfileInput) throws HttpBadRequestException {

        // * set up avatar property
        AvatarDto avatarDto = new AvatarDto(
                modifyUserProfileInput
        );

        // * update avatar property
        updateUserAvatar(userId, avatarDto);

        // Fetch the user record by email
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_ID.eq(userId))
                .fetchOne();

        if (userRecord == null) {
            throw new IllegalArgumentException("User not found with the given id: " + userId);
        }

        // Update the fields based on UserProfilePatchDto
        if (modifyUserProfileInput.getName() != null && !modifyUserProfileInput.getName().isEmpty()) {
            userRecord.setUserName(modifyUserProfileInput.getName());
        }

        // ! don't modify user's avatar here
//        if (modifyUserProfileInput.getAvatar() != null) {
//            userRecord.setUserAvatar(modifyUserProfileInput.getAvatar());
//        }

        // Update the user in the database
        userRecord.store();

        // Map updated UserRecord to UserProfileDto
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setId(String.valueOf(userRecord.getUserId()));
        updatedProfile.setEmail(userRecord.getUserEmail());
        updatedProfile.setName(userRecord.getUserName());
        // ! don't modify user's avatar here
        // updatedProfile.setAvatar(userRecord.getUserAvatar());
        updatedProfile.setThumbnail(userRecord.getUserThumbnail());

        return updatedProfile;
    }

    public UserProfileDto updateUserAvatar(Integer userId, String publicUrl) {
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_ID.eq(userId))
                .fetchOne();

        userRecord.setUserAvatar(publicUrl);
        userRecord.update();

        // Map updated UserRecord to UserProfileDto
        UserProfileDto updatedProfile = new UserProfileDto();
        updatedProfile.setId(String.valueOf(userRecord.getUserId()));
        updatedProfile.setEmail(userRecord.getUserEmail());
        updatedProfile.setName(userRecord.getUserName());
        updatedProfile.setAvatar(userRecord.getUserAvatar());
        updatedProfile.setThumbnail(userRecord.getUserThumbnail());

        return updatedProfile;
    }

    public void updateUserAvatarUrl(Integer userId, String publicUrl) throws HttpBadRequestException {
        int updatedRows = dslContext.update(USER)
                .set(USER.USER_AVATAR, publicUrl)
                .where(USER.USER_ID.eq(userId))
                .execute();
        if (updatedRows == 0) {
            throw new HttpBadRequestException("Failed to update avatar URL for user ID: " + userId);
        }
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
        UserRecord userRecord = dslContext.selectFrom(USER)
                .where(USER.USER_ID.eq(userId)).fetchOne();

        UserModal userModal = new UserModal();
        userModal.setId(String.valueOf(userRecord.getUserId()));
        userModal.setAvatar(userRecord.getUserAvatar());
        userModal.setEmail(userRecord.getUserEmail());
        userModal.setName(userRecord.getUserName());
        userModal.setThumbnail(userRecord.getUserThumbnail());
        userModal.setProvider(String.valueOf(userRecord.getUserProvider()));
        return userModal;

    }

    public UseravatarRecord getUserAvatar(Integer userId) throws HttpBadRequestException {
        UseravatarRecord useravatarRecord = dslContext.selectFrom(USERAVATAR).where(USERAVATAR.USER_ID.eq(userId)).fetchOne();
        if (useravatarRecord != null) return useravatarRecord;

        int inserted = dslContext.insertInto(USERAVATAR).set(USERAVATAR.USER_ID, userId).execute();
        // Step 4: Return the created user record (or fetch it from the database)
        if (inserted > 0) {
            // Fetch the user record after insertion
            return dslContext.selectFrom(USERAVATAR).where(USERAVATAR.USER_ID.eq(userId)).fetchOne();
        } else {
            throw new HttpBadRequestException("Failed to insert new avatar");
        }
    }

    public AvatarDto updateUserAvatar(Integer userId, AvatarDto avatarDto) throws HttpBadRequestException {
        UseravatarRecord useravatarRecord = getUserAvatar(userId);

        logger.debug(avatarDto.getFaceColor());
        // Update fields only if avatarDto values are not null
        if (avatarDto.getSex() != null) useravatarRecord.setSex(avatarDto.getSex().getSex());
        if (avatarDto.getFaceColor() != null) useravatarRecord.setFaceColor(avatarDto.getFaceColor());
        if (avatarDto.getEarSize() != null) useravatarRecord.setEarSize(avatarDto.getEarSize().getEarSize());
        if (avatarDto.getEyeStyle() != null) useravatarRecord.setEyeStyle(avatarDto.getEyeStyle().getEyeStyle());
        if (avatarDto.getNoseStyle() != null) useravatarRecord.setNoseStyle(avatarDto.getNoseStyle().getNoseStyle());
        if (avatarDto.getMouthStyle() != null)
            useravatarRecord.setMouthStyle(avatarDto.getMouthStyle().getMouthStyle());
        if (avatarDto.getShirtStyle() != null)
            useravatarRecord.setShirtStyle(avatarDto.getShirtStyle().getShirtStyle());
        if (avatarDto.getGlassesStyle() != null)
            useravatarRecord.setGlassesStyle(avatarDto.getGlassesStyle().getGlassesStyle());
        if (avatarDto.getHairColor() != null) useravatarRecord.setHairColor(avatarDto.getHairColor());
        if (avatarDto.getHairStyle() != null) useravatarRecord.setHairStyle(avatarDto.getHairStyle().getHairStyle());
        if (avatarDto.getHatStyle() != null) useravatarRecord.setHatStyle(avatarDto.getHatStyle().getHatStyle());
        if (avatarDto.getHatColor() != null) useravatarRecord.setHatColor(avatarDto.getHatColor());
        if (avatarDto.getEyeBrowStyle() != null)
            useravatarRecord.setEyeBrow(avatarDto.getEyeBrowStyle().getEyeBrowStyle());
        if (avatarDto.getShirtColor() != null) useravatarRecord.setShirtColor(avatarDto.getShirtColor());
        if (avatarDto.getBgColor() != null) useravatarRecord.setBgColor(avatarDto.getBgColor());

        int updatedRows = useravatarRecord.update();
        if (updatedRows == 0) {
            throw new HttpBadRequestException("Failed to update avatar");
        }

        return avatarDto;
    }
}
