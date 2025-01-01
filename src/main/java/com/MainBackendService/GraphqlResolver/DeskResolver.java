package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class DeskResolver {

    @Autowired
    private DeskGQLService deskGQLService;

    /**
     * GraphQL query resolver for fetching the list of desks with pagination.
     *
     * @param skip  The offset for pagination, default to 0.
     * @param limit The number of items per page, default to 30.
     * @return DeskPaginationResult containing the list of desks and pagination details.
     */
    @DgsQuery
    public DeskPaginationResult getDesks(Integer skip, Integer limit) {
        // Call DeskGQLService to fetch the paginated desks
        return deskGQLService.getDesks(skip, limit);
    }
}
