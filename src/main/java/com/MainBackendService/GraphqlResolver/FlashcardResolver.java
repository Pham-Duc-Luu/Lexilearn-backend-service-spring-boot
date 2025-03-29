package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.GraphqlDto.CreateFlashcardInput;
import com.MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import com.MainBackendService.dto.GraphqlDto.UpdateFlashcardInput;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.exception.HttpUnauthorizedException;
import com.MainBackendService.modal.FlashcardModal;
import com.MainBackendService.modal.SMModal;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.DeskService.DeskBelongToUserService;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.FlashcardService.FlashcardGQLService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.SpacedRepetitionSerivce.SM_2_GQLService;
import com.MainBackendService.service.UserService.UserService;
import com.MainBackendService.utils.HttpHeaderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@DgsComponent
public class FlashcardResolver {
    Logger logger = LogManager.getLogger(FlashcardResolver.class);
    @Autowired
    HttpHeaderUtil httpHeaderUtil;
    @Autowired
    private FlashcardGQLService flashcardGQLService;
    @Autowired
    private DeskGQLService deskGQLService;
    @Autowired
    private AccessTokenJwtService accessTokenJwtService;

    @Autowired
    private SM_2_GQLService sm_2_gqlService;

    @Autowired
    private DeskService deskService;
    @Autowired
    private FlashcardService flashcardService;


    @Autowired
    private DSLContext dslContext;

    @Autowired
    private UserService userService;


    @DgsMutation
    public FlashcardModal createFlashcard(@InputArgument CreateFlashcardInput input, DgsDataFetchingEnvironment dfe) throws HttpResponseException {

        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");
        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        if (!deskService.isUserOwnerOfDesk(userDetails.getId(), input.getDesk_id())) {
            throw new HttpUnauthorizedException();
        }
        return flashcardGQLService.createFlashcard(input);
    }


    @DgsMutation
    public FlashcardModal updateFlashcard(
            @InputArgument UpdateFlashcardInput input, DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");
        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        if (!deskService.isUserOwnerOfDesk(userDetails.getId(), input.getDesk_id())) {
            throw new HttpUnauthorizedException();
        }

        return flashcardGQLService.updateFlashcard(input);

    }

    @DgsMutation
    public Integer updateFlashcards(
            @InputArgument List<UpdateFlashcardInput> inputs, DgsDataFetchingEnvironment dfe) throws HttpResponseException {


        List<CompletableFuture<FlashcardModal>> futures = inputs.stream()
                .map(input -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return updateFlashcard(input, dfe);
                    } catch (HttpResponseException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .collect(Collectors.toList());

        // Wait for all async operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return inputs.size();

    }

    /**
     * GraphQL query resolver for fetching flashcards with pagination.
     *
     * @param skip   The offset for pagination, default is 0.
     * @param limit  The number of items per page, default is 30.
     * @param deskId The ID of the desk to filter flashcards.
     * @return FlashcardPaginationResult containing flashcards and pagination details.
     */
    @DgsQuery
    public FlashcardPaginationResult getFlashcards(Integer skip, Integer limit, Integer deskId) {
        return flashcardGQLService.getFlashcards(skip, limit, deskId);
    }

    @DgsData(parentType = "Flashcard", field = "SM")
    public SMModal getSpacedRepetitionModal(DgsDataFetchingEnvironment dfe) {
        FlashcardModal flashcardModal = dfe.getSource();
        return sm_2_gqlService.getSpacedRepetitionWithFlashcardId(Integer.valueOf(flashcardModal.getId()));
    }

    @DgsMutation
    public Number createFlashcards(@InputArgument List<CreateFlashcardInput> inputs,
                                   DgsDataFetchingEnvironment dfe) throws HttpResponseException {

        List<FlashcardModal> flashcardModals = inputs.stream()
                .map(input -> {
                    try {
                        return createFlashcard(input, dfe);
                    } catch (HttpResponseException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();


        return flashcardModals.size();
    }


    @DgsQuery
    public FlashcardPaginationResult getDeskFlashcards(@InputArgument Integer skip,
                                                       @InputArgument Integer limit,
                                                       @InputArgument Integer deskId) throws HttpResponseException, JsonProcessingException {
        // Step 1: Fetch ordered flashcards from DeskService
        List<FlashcardModal> flashcardRecords = flashcardService.getFlashcardsInDesk(deskId, FlashcardModal.class);
        // Step 2: Apply pagination
        int effectiveSkip = skip != null && skip >= 0 ? skip : 0; // Default to 0 if null or negative
        int effectiveLimit = limit != null && limit > 0 ? limit : flashcardRecords.size(); // Default to full size if null or invalid

        // Ensure skip doesn’t exceed list size
        if (effectiveSkip >= flashcardRecords.size()) {
            return new FlashcardPaginationResult(null, flashcardRecords.size(), effectiveSkip, effectiveLimit);
        }
        // Calculate sublist bounds
        int fromIndex = effectiveSkip;
        int toIndex = Math.min(fromIndex + effectiveLimit, flashcardRecords.size());
        List<FlashcardModal> flashcardModals = flashcardRecords.subList(fromIndex, toIndex);
        // Step 4: Return FlashcardPaginationResult
        return new FlashcardPaginationResult(
                flashcardModals,          // Paginated list
                flashcardRecords.size(),  // Total count
                effectiveSkip,            // Applied skip
                effectiveLimit            // Applied limit
        );
    }

    @DgsQuery
    public FlashcardPaginationResult getDeskNeedReviewFlashcard(@InputArgument Integer skip,
                                                                @InputArgument Integer limit,
                                                                @InputArgument Integer deskId) throws HttpResponseException, JsonProcessingException {
        // Step 1: Fetch ordered flashcards from DeskService
        List<FlashcardModal> flashcardRecords = flashcardService.getNeedToReviewFlashcards(deskId, FlashcardModal.class);
        // Step 2: Apply pagination
        int effectiveSkip = skip != null && skip >= 0 ? skip : 0; // Default to 0 if null or negative
        int effectiveLimit = limit != null && limit > 0 ? limit : flashcardRecords.size(); // Default to full size if null or invalid

        // Ensure skip doesn’t exceed list size
        if (effectiveSkip >= flashcardRecords.size()) {
            return new FlashcardPaginationResult(null, flashcardRecords.size(), effectiveSkip, effectiveLimit);
        }
        // Calculate sublist bounds
        int fromIndex = effectiveSkip;
        int toIndex = Math.min(fromIndex + effectiveLimit, flashcardRecords.size());
        List<FlashcardModal> flashcardModals = flashcardRecords.subList(fromIndex, toIndex);
        // Step 4: Return FlashcardPaginationResult
        return new FlashcardPaginationResult(
                flashcardModals,          // Paginated list
                flashcardRecords.size(),  // Total count
                effectiveSkip,            // Applied skip
                effectiveLimit            // Applied limit
        );
    }


    @DgsMutation
    public Integer deleteFlashcard(@InputArgument Integer flashcardId, @InputArgument Integer deskId, @NotNull DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        new DeskBelongToUserService(userDetails.getId(), dslContext).deleteFlashcardInDesk(deskId, flashcardId);

        return flashcardId;
    }

}
