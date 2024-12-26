package com.MainBackendService.service;

import com.jooq.sample.model.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jooq.sample.model.tables.UserToken.USER_TOKEN;

@Service
public class UserTokenService {


    private final DSLContext dslContext;

    @Autowired
    public UserTokenService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<UserRecord> findTokensByUserId(Integer userId) {
        // Fetch user tokens by userId
        return dslContext.selectFrom(USER_TOKEN)
                .where(USER_TOKEN.UT_USER_ID.eq(userId))
                .fetchInto(UserRecord.class);
    }

}
