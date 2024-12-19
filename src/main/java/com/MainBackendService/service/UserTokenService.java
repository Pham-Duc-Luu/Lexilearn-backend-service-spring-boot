package com.MainBackendService.service;

import com.MainBackendService.model.UserToken;
import com.MainBackendService.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;

    public UserTokenService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public List<UserToken> findTokensByUserId(Integer userId) {
        return userTokenRepository.findByUser_UserId(userId);
    }
}
