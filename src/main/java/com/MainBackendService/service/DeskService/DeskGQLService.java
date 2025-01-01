package com.MainBackendService.service.DeskService;

import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.modal.DeskModal;
import com.MainBackendService.service.UserService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jooq.sample.model.tables.Desk.DESK;


@Service
public class DeskGQLService {

    private final UserService userService;
    @Autowired
    private DSLContext dslContext;

    @Autowired

    public DeskGQLService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches a list of desks with pagination details.
     *
     * @param skip  The offset for pagination, default to 0.
     * @param limit The number of items per page, default to 30.
     * @return DeskPaginationResult containing the list of desks and pagination details.
     */
    
//    @GraphQLQuery(name = "getDesks")
    public DeskPaginationResult getDesks(Integer skip, Integer limit) {
        // Set default values for skip and limit if not provided
        if (skip == null) {
            skip = 0;
        }
        if (limit == null) {
            limit = 30;
        }
        // Fetch the total number of desks
        int total = dslContext.selectCount()
                .from(DESK)
                .fetchOne(0, int.class); // Get the total count

        // Fetch the list of desks with pagination (skip/limit)
        List<DeskModal> desks = dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_IS_PUBLIC.eq((byte) 1)) // Explicitly cast 1 to byte                .limit(limit)
                .offset(skip).fetchInto(DeskModal.class);
        // Map to DeskModal

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }

    public DeskModal getDesk(Integer id) {
        return dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_ID.eq(id))
                .fetchOneInto(DeskModal.class);
    }
}
