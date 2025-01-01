package com.MainBackendService.service;


import com.MainBackendService.utils.JwtUtil;
import com.auth0.jwt.algorithms.Algorithm;
import com.jooq.sample.model.enums.UserTokenUtType;
import com.jooq.sample.model.tables.records.UserRecord;
import com.jooq.sample.model.tables.records.UserTokenRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.jooq.sample.model.tables.User.USER;
import static com.jooq.sample.model.tables.UserToken.USER_TOKEN;

@Service
public class TokenService {
    private final JwtUtil jwtUtil = new JwtUtil();
    private final AccessTokenJwtService accessTokenJwtService;
    private final RefreshTokenJwtService refreshTokenJwtService;
    private final DSLContext dslContext;
    Logger logger = LogManager.getLogger(TokenService.class);
    @Value("${private.key}")
    private String privateKey;
    @Value("${private.time}")
    private String privateTime;
    @Value("${public.key}")
    private String publicKey;
    @Value("${public.time}")
    private String publicTime;
    @Value("${opt.expired.in}")
    private String optExpiredIn;

    @Autowired
    public TokenService(

            AccessTokenJwtService accessTokenJwtService,
            RefreshTokenJwtService refreshTokenJwtService, DSLContext dslContext
    ) {

        this.accessTokenJwtService = accessTokenJwtService;
        this.refreshTokenJwtService = refreshTokenJwtService;
        this.dslContext = dslContext;
    }


    @Async
    public void setOtp(String email, String otp) {

        // Step 1: Check if the user exists by email
        Integer userId = dslContext.select(USER.USER_ID)
                .from(USER)
                .where(USER.USER_EMAIL.eq(email))
                .fetchOneInto(Integer.class);

        if (userId == null) {
            throw new IllegalArgumentException("User with the provided email does not exist");
        }

        // Step 4: Insert OTP into the User_Token table
        UserTokenRecord userTokenRecord = dslContext.insertInto(USER_TOKEN)
                .set(USER_TOKEN.UT_TYPE, UserTokenUtType.OTP) // Set the type to "OTP"
                .set(USER_TOKEN.UT_TEXT, otp)
                .set(USER_TOKEN.UT_USER_ID, userId)
                .set(USER_TOKEN.UT_EXPIRED_AT, LocalDateTime.now().plusSeconds(Long.parseLong(optExpiredIn)))
                .returning(USER_TOKEN.UT_ID)
                .fetchOne();

    }

    public String setAccessToken(UserRecord user) {

        return accessTokenJwtService.genToken(user.getUserName(), user.getUserEmail());
    }


    public String setRefreshToken(UserRecord user) {

        // * set the algorithm for the jwt token
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        String refreshToken = refreshTokenJwtService.genToken(user.getUserName(), user.getUserEmail());


        // Step 1: Check if the user exists by email
        Integer userId = dslContext.select(USER.USER_ID)
                .from(USER)
                .where(USER.USER_EMAIL.eq(user.getUserEmail()))
                .fetchOneInto(Integer.class);

        if (userId == null) {
            throw new IllegalArgumentException("User with the provided email does not exist");
        }

        // Step 4: Insert OTP into the User_Token table
        UserTokenRecord userTokenRecord = dslContext.insertInto(USER_TOKEN)
                .set(USER_TOKEN.UT_TYPE, UserTokenUtType.REFRESH_TOKEN) // Set the type to "OTP"
                .set(USER_TOKEN.UT_TEXT, refreshToken)
                .set(USER_TOKEN.UT_USER_ID, userId)
                .set(USER_TOKEN.UT_EXPIRED_AT, (LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + Integer.valueOf(privateTime)), ZoneId.systemDefault())))
                .returning(USER_TOKEN.UT_ID)
                .fetchOne();


        return refreshToken;

    }

    public UserRecord findUserWithRefreshToken(String refreshToken) {
        return dslContext.select(USER.fields())
                .from(USER.join(USER_TOKEN).on(USER.USER_ID.eq(USER_TOKEN.UT_USER_ID)))
                .where(USER_TOKEN.UT_TEXT.eq(refreshToken))
                .fetchOneInto(UserRecord.class);
    }
}
