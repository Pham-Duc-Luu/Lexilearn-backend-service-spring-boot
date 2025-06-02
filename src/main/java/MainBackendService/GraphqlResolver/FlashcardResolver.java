package MainBackendService.GraphqlResolver;

import MainBackendService.Microservice.ImageServerService.service.AudioService;
import MainBackendService.Microservice.ImageServerService.service.ImageService;
import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.exception.HttpUnauthorizedException;
import MainBackendService.modal.FlashcardModal;
import MainBackendService.modal.SMModal;
import MainBackendService.service.AccessTokenJwtService;
import MainBackendService.service.DeskService.DeskFlashcardsLinkedListOperation;
import MainBackendService.service.DeskService.DeskGQLService;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardGQLService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.SpacedRepetitionSerivce.SM_2_GQLService;
import MainBackendService.service.UserService.UserService;
import MainBackendService.utils.HttpHeaderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jooq.sample.model.tables.records.DeskRecord;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    private ImageService imageService;


    @Autowired
    private AudioService audioService;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private UserService userService;

    @Autowired
    private DeskFlashcardsLinkedListOperation deskFlashcardsLinkedListOperation;


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
        return sm_2_gqlService.getSpacedRepetitionWithFlashcardId(flashcardModal.getId());
    }

    @DgsQuery
    public FlashcardPaginationResult getLinkedListFlashcard(@InputArgument Integer skip,
                                                            @InputArgument Integer limit,
                                                            @InputArgument Integer deskId) throws HttpResponseException {
        DeskRecord deskRecord = deskService.getDeskById(deskId);

        if (deskRecord == null) {
            throw new HttpNotFoundException("Desk not found");
        }

        List<Integer> flashcardIdList = deskFlashcardsLinkedListOperation.linkedListTraverse(deskRecord);
        int start = Math.min(skip, flashcardIdList.size());
        int end = Math.min(start + limit, flashcardIdList.size());

        List<FlashcardModal> flashcardModalList = new ArrayList<>();

        for (Integer flashcardId : flashcardIdList) {
            try {
                flashcardModalList.add(flashcardService.getFlashcard(flashcardId));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
                break;
            }
        }

        return new FlashcardPaginationResult(flashcardModalList, flashcardIdList.size(), skip, limit);
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
                                                                @InputArgument Integer deskId, DgsDataFetchingEnvironment dfe) throws HttpResponseException, JsonProcessingException {


        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        // * check if user can query this or not
        if (!deskService.isUserOwnerOfDesk(userDetails.getId(), deskId)) {
            throw new HttpUnauthorizedException("You are not allow to view need-to-review-flashcard of this desk");
        }

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


        // * query media file for s3

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

        // Step 4: Return FlashcardPaginationResult
        return new FlashcardPaginationResult(
                flashcardModals,          // Paginated list
                flashcardRecords.size(),  // Total count
                effectiveSkip,            // Applied skip
                effectiveLimit            // Applied limit
        );
    }


}
