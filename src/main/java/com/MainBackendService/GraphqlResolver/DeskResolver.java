package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.DeskDto;
import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.dto.GraphqlDto.DeskQueryFilter;
import com.MainBackendService.dto.GraphqlDto.DeskQuerySort;
import com.MainBackendService.dto.GraphqlDto.SearchDeskArg;
import com.MainBackendService.exception.HttpBadRequestException;
import com.MainBackendService.exception.HttpNotFoundException;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.modal.DeskModal;
import com.MainBackendService.modal.FlashcardModal;
import com.MainBackendService.modal.UserModal;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.UserService.UserService;
import com.MainBackendService.utils.HttpHeaderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import io.swagger.v3.oas.annotations.Operation;
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
    private DeskService deskService;

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
        return userService.findUserById(Integer.valueOf(deskModal.getOwnerId()));
    }

    @DgsData(parentType = "Desk", field = "flashcardQuantity")
    public Integer flashcardQuantity(DgsDataFetchingEnvironment dfe) {
        DeskModal deskModal = dfe.getSource();

        return flashcardService.getFlashcardQuantityInDesk(Integer.parseInt(deskModal.getId()));
    }

    @DgsData(parentType = "Desk", field = "flashcards")
    public List<FlashcardModal> flashcards(DgsDataFetchingEnvironment dfe) throws HttpResponseException, JsonProcessingException {
        DeskModal deskModal = dfe.getSource();
        logger.debug(deskModal.getId());
        if (deskModal.getId() == null) return null;
        return flashcardService.getFlashcardsInDesk(Integer.valueOf(deskModal.getId()), FlashcardModal.class);
    }

    @DgsQuery
    @Operation(summary = "this is a api for query user's desk", description = "")
    public DeskPaginationResult getUserDesks(@InputArgument Integer skip,
                                             @InputArgument Integer limit,
                                             @InputArgument DeskQuerySort sort,
                                             @InputArgument DeskQueryFilter filter,
                                             @InputArgument SearchDeskArg searchArg,

                                             DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        // Call DeskGQLService to fetch the paginated desks

        if (searchArg == null) return deskGQLService.getDeskByUserId(userDetails.getId(), skip, limit, sort, filter);

        List<DeskDto> deskDtos = deskGQLService.searchUserDesksByText(searchArg, userDetails.getId());
        return new DeskPaginationResult(deskDtos.stream().map(deskDto -> new DeskModal(deskDto)).toList(), deskDtos.size(), 0, deskDtos.size());

    }

    @DgsQuery
    @Operation(summary = "this is a api for searching desk")
    public DeskPaginationResult searchDesk(@InputArgument Integer skip,
                                           @InputArgument Integer limit,
                                           @InputArgument SearchDeskArg searchArg
    ) {
        //  Apply pagination
        if (skip == null) {
            skip = 0;
        }
        if (limit == null || limit > 30) limit = 30;

        List<DeskDto> deskDtos = deskGQLService.searchDesksByText(skip, limit, searchArg);
        List<DeskModal> deskModalList = deskDtos.stream().map(deskDto -> new DeskModal(deskDto)).toList();
        return new DeskPaginationResult(deskModalList, 0, skip, limit);
    }

    @DgsQuery
    @Operation(summary = "this is a api for query user's desk")
    public DeskModal getDesk(@InputArgument Integer id, DgsDataFetchingEnvironment dfe) throws HttpResponseException {

        DeskRecord deskRecord = deskService.getDeskById(id);

        if (deskRecord == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        // * check if the desk is public for everyone to see or not
        if (deskRecord.getDeskIsPublic() == 1) return new DeskModal(deskRecord);

        // * else, check the user who query it
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        if (!deskRecord.getDeskOwnerId().equals(userDetails.getId()))
            throw new HttpBadRequestException("You are not allow to view this desk");

        return new DeskModal(deskRecord);

    }
}
