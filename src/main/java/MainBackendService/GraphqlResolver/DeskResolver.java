package MainBackendService.GraphqlResolver;

import MainBackendService.Microservice.ImageServerService.service.AudioService;
import MainBackendService.Microservice.ImageServerService.service.ImageService;
import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.DeskDto;
import MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import MainBackendService.dto.GraphqlDto.DeskQueryFilter;
import MainBackendService.dto.GraphqlDto.DeskQuerySort;
import MainBackendService.dto.GraphqlDto.SearchDeskArg;
import MainBackendService.exception.HttpBadRequestException;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.modal.DeskModal;
import MainBackendService.modal.FlashcardModal;
import MainBackendService.modal.UserModal;
import MainBackendService.service.AccessTokenJwtService;
import MainBackendService.service.DeskService.DeskGQLService;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.UserService.UserService;
import MainBackendService.utils.HttpHeaderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Component
@DgsComponent
public class DeskResolver {
    Logger logger = LogManager.getLogger(DeskResolver.class);
    @Autowired
    HttpHeaderUtil httpHeaderUtil;
    @Autowired
    @Qualifier("dgsAsyncTaskExecutor")
    Executor executor;
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

    @Autowired
    private ImageService imageService;

    @Autowired
    private AudioService audioService;

    /**
     * GraphQL query resolver for fetching the list of desks with pagination.
     *
     * @param skip  The offset for pagination, default to 0.
     * @param limit The number of items per page, default to 30.
     * @return DeskPaginationResult containing the list of desks and pagination
     * details.
     */
    @DgsQuery
    public DeskPaginationResult getDesks(@InputArgument Integer skip,
                                         @InputArgument Integer limit,
                                         @InputArgument DeskQuerySort sort) {
        logger.debug(sort);
        // Call DeskGQLService to fetch the paginated desks
        return deskGQLService.getDesks(skip, limit, sort, null);
    }

    @DgsData(parentType = "Desk", field = "needToReviewFlashcardQuantity")
    public CompletableFuture<Integer> needToReviewFlashcardQuantity(DgsDataFetchingEnvironment dfe) {
        logger.debug("start");
        DeskModal deskModal = dfe.getSource();

        return CompletableFuture.supplyAsync(() -> {
            List<FlashcardModal> flashcardRecords = flashcardService.getNeedToReviewFlashcards(
                    Integer.valueOf(deskModal.getId()), FlashcardModal.class
            );
            logger.debug(flashcardRecords.size());
            return flashcardRecords.size();
        }, executor);
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
    public List<FlashcardModal> flashcards(DgsDataFetchingEnvironment dfe)
            throws HttpResponseException, JsonProcessingException {
        DeskModal deskModal = dfe.getSource();

        if (deskModal.getId() == null)
            return null;

        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        List<FlashcardModal> flashcardModals = flashcardService.getFlashcardsInDesk(Integer.valueOf(deskModal.getId()),
                FlashcardModal.class);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (FlashcardModal flashcard : flashcardModals) {
            // Front image
            futures.add(imageService.getPresignUrlAsync(flashcard.getFront_image(), tokens.getFirst())
                    .thenAccept(url -> flashcard.setFront_image(url)));

            // Back image
            futures.add(imageService.getPresignUrlAsync(flashcard.getBack_image(), tokens.getFirst())
                    .thenAccept(url -> flashcard.setBack_image(url)));

            // Front sound
            futures.add(audioService.getPresignUrlAsync(flashcard.getFront_sound(), tokens.getFirst())
                    .thenAccept(url -> flashcard.setFront_sound(url)));

            // Back sound
            futures.add(audioService.getPresignUrlAsync(flashcard.getBack_sound(), tokens.getFirst())
                    .thenAccept(url -> flashcard.setBack_sound(url)));
        }

        // Wait for all to complete (with timeout logic if needed)
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Timeout or failure while fetching presigned URLs", e);
        }
        return flashcardModals;
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


        if (searchArg == null)
            return deskGQLService.getDeskByUserId(userDetails.getId(), skip, limit, sort, filter);

        List<DeskDto> deskDtos = deskGQLService.searchUserDesksByText(searchArg, userDetails.getId());
        return new DeskPaginationResult(deskDtos.stream().map(deskDto -> new DeskModal(deskDto)).toList(),
                deskDtos.size(), 0, deskDtos.size());

    }

    @DgsQuery
    @Operation(summary = "this is a api for searching desk")
    public DeskPaginationResult searchDesk(@InputArgument Integer skip,
                                           @InputArgument Integer limit,
                                           @InputArgument SearchDeskArg searchArg) {
        // Apply pagination
        if (skip == null) {
            skip = 0;
        }
        if (limit == null || limit > 30)
            limit = 30;

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
        if (deskRecord.getDeskIsPublic() == 1)
            return new DeskModal(deskRecord);

        // * else, check the user who query it
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        if (!deskRecord.getDeskOwnerId().equals(userDetails.getId()))
            throw new HttpBadRequestException("You are not allow to view this desk");

        return new DeskModal(deskRecord);

    }
}
