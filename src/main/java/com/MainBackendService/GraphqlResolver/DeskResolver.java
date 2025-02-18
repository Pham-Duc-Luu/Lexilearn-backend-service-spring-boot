package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.dto.GraphqlDto.DeskQueryFilter;
import com.MainBackendService.dto.GraphqlDto.DeskQuerySort;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.modal.DeskModal;
import com.MainBackendService.modal.UserModal;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.UserService;
import com.MainBackendService.utils.HttpHeaderUtil;
import com.netflix.graphql.dgs.*;
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

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserService userService;

    /**
     * GraphQL query resolver for fetching the list of desks with pagination.
     *
     * @param skip  The offset for pagination, default to 0.
     * @param limit The number of items per page, default to 30.
     * @return DeskPaginationResult containing the list of desks and pagination details.
     */
    @DgsQuery
    public DeskPaginationResult getDesks(@InputArgument Integer skip,
                                         @InputArgument Integer limit,
                                         @InputArgument DeskQuerySort sort
    ) {
        logger.debug(sort);
        // Call DeskGQLService to fetch the paginated desks
        return deskGQLService.getDesks(skip, limit, sort, null);
    }

    @DgsData(parentType = "Desk", field = "owner")
    public UserModal owner(DgsDataFetchingEnvironment dfe) {
        DeskModal deskModal = dfe.getSource();
        logger.debug(deskModal);
        return userService.findUserById(Integer.valueOf(deskModal.getOwnerId()));
    }

    @DgsData(parentType = "Desk", field = "flashcardQuantity")
    public Integer flashcardQuantity(DgsDataFetchingEnvironment dfe) {
        DeskModal deskModal = dfe.getSource();

        return flashcardService.getFlashcardQuantityInDesk(Integer.parseInt(deskModal.getId()));
    }

    @DgsQuery
    public DeskPaginationResult getUserDesks(@InputArgument Integer skip,
                                             @InputArgument Integer limit,
                                             @InputArgument DeskQuerySort sort,
                                             @InputArgument DeskQueryFilter filter,
                                             DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");


        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        // Call DeskGQLService to fetch the paginated desks
        return deskGQLService.getDeskByUserId(userDetails.getId(), skip, limit, sort, filter);
    }

}
