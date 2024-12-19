package com.MainBackendService.service;

import com.MainBackendService.model.User;
import com.MainBackendService.model.UserToken;
import com.MainBackendService.model.UserTokenType;
import com.MainBackendService.repository.UserRepository;
import com.MainBackendService.repository.UserTokenRepository;
import com.MainBackendService.utils.JwtUtil;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;
    private final JwtUtil jwtUtil = new JwtUtil();
    private final UserRepository userRepository; // To fetch the user, if not already provided
    private final AccessTokenJwtService accessTokenJwtService;
    private final RefreshTokenJwtService refreshTokenJwtService;
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
            UserTokenRepository userTokenRepository,
            UserRepository userRepository,
            AccessTokenJwtService accessTokenJwtService,
            RefreshTokenJwtService refreshTokenJwtService
    ) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
        this.accessTokenJwtService = accessTokenJwtService;
        this.refreshTokenJwtService = refreshTokenJwtService;
    }


    @Async
    public void setOtp(String email, String otp) {
        Optional<User> foundUser = userRepository.findByUserEmail(email);

        User existUser = foundUser.orElseThrow(() -> new RuntimeException("User not found"));

        UserToken otpToken = new UserToken();
        otpToken.setUTText(otp);
        otpToken.setUTType(UserTokenType.OTP); // Enum for the type of token (e.g., OTP, PASSWORD_RESET)
        otpToken.setUTExpiredAt(LocalDateTime.now().plusSeconds(Long.parseLong(optExpiredIn))); // OTP expires in 10 minutes
        otpToken.setUser(existUser);
        userTokenRepository.save(otpToken);
        logger.debug("save otp to database");
    }

    public String setAccessToken(User user) {

        return accessTokenJwtService.genToken(user.getUserName(), user.getUserEmail());
    }


    public String setRefreshToken(User user) {

        // * set the algorithm for the jwt token
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        String refreshToken = refreshTokenJwtService.genToken(user.getUserName(), user.getUserEmail());


        UserToken newToken = new UserToken();

        newToken.setUTText(refreshToken);
        newToken.setUser(user);
        newToken.setUTExpiredAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + Integer.valueOf(privateTime)), ZoneId.systemDefault()));
        newToken.setUTType(UserTokenType.REFRESHTOKEN);
        // * save the user's refresh token to database
        userTokenRepository.save(newToken);
        return refreshToken;

    }


}
