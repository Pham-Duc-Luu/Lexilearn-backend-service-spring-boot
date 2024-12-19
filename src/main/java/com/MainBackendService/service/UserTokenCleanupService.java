package com.MainBackendService.service;

import com.MainBackendService.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserTokenCleanupService {
    @Autowired
    private UserTokenRepository userTokenRepository;

    @Scheduled(fixedRate = 3600000)  // Run every hour
    public void cleanupExpiredTokens() {
        userTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

}
