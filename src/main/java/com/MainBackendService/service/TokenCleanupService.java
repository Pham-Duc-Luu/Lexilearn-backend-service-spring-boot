package com.MainBackendService.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.jooq.sample.model.tables.UserToken.USER_TOKEN;

@Service
public class TokenCleanupService {

    private final DSLContext dslContext;

    @Autowired
    public TokenCleanupService(DSLContext dslContext) {

        this.dslContext = dslContext;
    }

    @Scheduled(fixedRate = 3600000)  // Run every hour
    public void cleanupExpiredTokens() {
        // Get the current time
        java.time.LocalDateTime currentTime = java.time.LocalDateTime.now();

        // Delete tokens that have expired
        int rowsDeleted = dslContext.deleteFrom(USER_TOKEN)
                .where(USER_TOKEN.UT_EXPIRED_AT.lt(currentTime))
                .execute();
    }
}
