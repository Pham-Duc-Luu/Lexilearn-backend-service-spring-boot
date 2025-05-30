package MainBackendService.GraphqlResolver;

import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.DeskDto;
import MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import MainBackendService.dto.GraphqlDto.SearchDeskArg;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.modal.DeskModal;
import MainBackendService.service.AccessTokenJwtService;
import MainBackendService.service.DeskService.DeskGQLService;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardGQLService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.UserService.UserService;
import MainBackendService.utils.HttpHeaderUtil;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DgsComponent
public class userPrivateResolver {
    Logger logger = LogManager.getLogger(userPrivateResolver.class);
    @Autowired
    HttpHeaderUtil httpHeaderUtil;

    @Autowired
    private DeskService deskService;

    @Autowired
    private FlashcardGQLService flashcardGQLService;

    @Autowired
    private DeskGQLService deskGQLService;
    @Autowired
    private AccessTokenJwtService accessTokenJwtService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserService userService;

    @DgsQuery
    @Operation(summary = "this is a api for searching desk that belong to user")
    public DeskPaginationResult userPrivateSearchDesk(@InputArgument Integer skip,
                                                      @InputArgument Integer limit,
                                                      @InputArgument SearchDeskArg searchArg,
                                                      DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        //  Apply pagination
        if (skip == null) {
            skip = 0;
        }
        if (limit == null || limit > 30) limit = 30;

        List<DeskDto> deskDtos = deskGQLService.searchUserDesksByText(skip, limit, searchArg, userDetails.getId());
        List<DeskModal> deskModalList = deskDtos.stream().map(deskDto -> new DeskModal(deskDto)).toList();
        return new DeskPaginationResult(deskModalList, 0, skip, limit);
    }


//    @DgsMutation
//    @Operation()
//    public DeskModal userPrivateUpdateDeskAndFlashcards(@InputArgument UpdateDeskInput desk, @InputArgument List<UpdateFlashcardInput> flashcards, DgsDataFetchingEnvironment dfe) throws HttpResponseException, JsonProcessingException {
//        DgsRequestData requestData = dfe.getDgsContext().getRequestData();
//
//        List<String> tokens = requestData.getHeaders().get("Authorization");
//
//        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);
//
//
//        // * check if the user have right to modify the desk
//        if (!deskService.isUserOwnerOfDesk(userDetails.getId(), Integer.valueOf(desk.getId())))
//            throw new HttpUnauthorizedException("You are not allow to modify this desk");
//
//
//        // * update or create flashcard
//        List<Integer> updatedFlashcardIdList = new ArrayList<Integer>();
//
//        for (int i = 0; i < flashcards.size(); i++) {
//            FlashcardModal flashcardModal = new FlashcardModal(flashcards.get(i));
//            flashcardModal.setDesk_position(i + 1);
//
//            if (flashcardModal.getId() == null) {
//                updatedFlashcardIdList.add(flashcardService.createFlashcard(Integer.valueOf(desk.getId()), flashcardModal).getFlashcardId());
//            } else {
//                updatedFlashcardIdList.add(flashcardService.updateFlashcard(flashcardModal).getId());
//            }
//        }
//
//        // * remove other flashcards that is not in the update list
//        List<Integer> allFlashcardIds =
//                flashcardService.getFlashcardsInDesk(Integer.valueOf(desk.getId()))
//                        .stream()
//                        .map(i -> i.getFlashcardId())
//                        .toList();
//
//        for (int i = 0; i < allFlashcardIds.size(); i++) {
//            Integer id = allFlashcardIds.get(i);
//            if (!updatedFlashcardIdList.contains(id)) {
//                flashcardService.deleteFlashcard(Integer.valueOf(desk.getId()), id);
//            }
//        }
////
//
////        flashcardIds.removeAll(updatedFlashcardIdList);
////        flashcardIds.stream().forEach(id -> {
////            flashcardService.deleteFlashcard(Integer.valueOf(desk.getId()), id);
////        });
//        // * update desk
//        DeskDto updatedDesk = new DeskDto(desk);
//
//        DeskRecord updatedDeskRecord = deskService.updateDesk(Integer.valueOf(desk.getId()), updatedDesk);
//        logger.debug(updatedDesk.toString());
//        return null;
//
//    }
//

}
