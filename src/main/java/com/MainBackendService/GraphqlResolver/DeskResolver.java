package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.MainBackendService.utils.HttpHeaderUtil;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DgsComponent
public class DeskResolver {
    Logger logger = LogManager.getLogger(DeskResolver.class);
    @Autowired
    HttpHeaderUtil httpHeaderUtil;

    @Autowired
    private DeskGQLService deskGQLService;
    @Autowired
    private AccessTokenJwtService accessTokenJwtService;

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

    @DgsQuery
    public DeskPaginationResult getUserDesks(@InputArgument Integer skip, @InputArgument Integer limit, DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");


        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        // Call DeskGQLService to fetch the paginated desks
        return deskGQLService.getDeskByUserId(userDetails.getId(), skip, limit);
    }

}
