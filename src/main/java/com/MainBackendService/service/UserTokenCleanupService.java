package com.MainBackendService.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.jooq.sample.model.tables.UserToken.USER_TOKEN;

@Service
public class UserTokenCleanupService {


    private final DSLContext dslContext;

    @Autowired
    public UserTokenCleanupService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Scheduled(fixedRate = 3600000)  // Run every hour
    public void cleanupExpiredTokens() {
        // Delete tokens that are expired (where UT_expired_at is before the current time)
        dslContext.deleteFrom(USER_TOKEN)
                .where(USER_TOKEN.UT_EXPIRED_AT.lessThan(LocalDateTime.now()))
                .execute();

        // Optionally, log the cleanup process
        System.out.println("Expired tokens have been cleaned up.");
    }

}
