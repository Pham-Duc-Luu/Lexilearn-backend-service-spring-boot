package com.MainBackendService.service;

import com.MainBackendService.controller.User.Auth;
import com.MainBackendService.model.User;
import com.MainBackendService.model.UserToken;
import com.MainBackendService.model.UserTokenType;
import com.MainBackendService.repository.UserRepository;
import com.MainBackendService.repository.UserTokenRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.MainBackendService.utils.JwtUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenService {
    Logger logger = LogManager.getLogger(TokenService.class);

    @Autowired
    public TokenService(UserTokenRepository userTokenRepository,UserRepository userRepository) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
    }

    private final UserTokenRepository userTokenRepository;


    private final UserRepository userRepository; // To fetch the user, if not already provided

    @Value("${private.key}")
    private String privateKey;
    @Value("${private.time}")
    private String privateTime;


    @Value("${public.key}")
    private String publicKey;
    @Value("${public.time}")
    private String publicTime;

    public String saveAccessToken(User user) {

        JSONObject jo = new JSONObject();
        jo.put("email" , user.getUserEmail()).put("name",user.getUserName());

        return JwtUtil.createToken(jo.toString(), publicKey, Integer.valueOf(publicTime));
    }

    public UserToken saveRefreshToken(User user) {
        UserToken newToken = new UserToken();

        JSONObject jo = new JSONObject();
        jo.put("email" , user.getUserEmail());
        jo.put("name",user.getUserName());

        logger.debug(jo.toString());

        newToken.setUTText(JwtUtil.createToken(jo.toString(), privateKey, Integer.valueOf(privateTime)));
        newToken.setUser(user);
        newToken.setUTExpiredAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + Integer.valueOf(privateTime)), ZoneId.systemDefault()));
        newToken.setUTType(UserTokenType.REFRESHTOKEN);

        return userTokenRepository.save(newToken);
    }
}
