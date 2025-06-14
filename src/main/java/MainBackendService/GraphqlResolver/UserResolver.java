package MainBackendService.GraphqlResolver;

import MainBackendService.Microservice.ImageServerService.service.ImageServerClient;
import MainBackendService.Microservice.ImageServerService.service.ImageService;
import MainBackendService.dto.AccessTokenDetailsDto;
import MainBackendService.dto.GraphqlDto.AvatarDto;
import MainBackendService.dto.GraphqlDto.ModifyUserProfileInput;
import MainBackendService.dto.UserProfileDto;
import MainBackendService.exception.HttpNotFoundException;
import MainBackendService.exception.HttpResponseException;
import MainBackendService.service.AccessTokenJwtService;
import MainBackendService.service.DeskService.DeskGQLService;
import MainBackendService.service.DeskService.DeskService;
import MainBackendService.service.FlashcardService.FlashcardService;
import MainBackendService.service.UserService.UserService;
import MainBackendService.utils.HttpHeaderUtil;
import com.jooq.sample.model.tables.records.UseravatarRecord;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import feign.FeignException;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Component
@DgsComponent
public class UserResolver {
    Logger logger = LogManager.getLogger(UserResolver.class);
    @Autowired
    HttpHeaderUtil httpHeaderUtil;

    @Autowired
    private ImageServerClient imageServerClient;
    @Autowired
    private DeskService deskService;
    @Autowired
    private DeskGQLService deskGQLService;
    @Autowired
    private AccessTokenJwtService accessTokenJwtService;
    @Autowired
    private ImageService imageService;

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private UserService userService;

    @DgsData(parentType = "User", field = "avatarProperty")
    public AvatarDto getAvatarDto(DgsDataFetchingEnvironment dfe) throws HttpResponseException, BadRequestException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);
        UseravatarRecord useravatarRecord = userService.getUserAvatar(userDetails.getId());

        return new AvatarDto(useravatarRecord);
    }

    @DgsQuery
    public UserProfileDto getUserProfile(DgsDataFetchingEnvironment dfe) throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);

        Optional<UserProfileDto> userProfileDto = userService.getUserProfile(userDetails.getEmail());


        if (userProfileDto.isEmpty())
            throw new HttpNotFoundException();

        // * check if the image url still alive
        String avatar = userProfileDto.get().getAvatar();

        if (avatar == null || avatar.isBlank()) {
            return userProfileDto.get(); // Or handle the missing avatar case more appropriately
        }

        // Extract the path from the URL
        try {
            URI uri = new URI(userProfileDto.get().getAvatar());
            String path = uri.getPath();

            // Extract filename (last part of the path)
            String fileName = path.substring(path.lastIndexOf("/") + 1);


            String presignedAvatarUrl = imageServerClient.getUserImage(tokens.getFirst(), fileName).getUrl();

            if (presignedAvatarUrl == null) {
                return userProfileDto.get();
            }

            userService.updateUserAvatarUrl(userDetails.getId(), presignedAvatarUrl);
            userProfileDto.get().setAvatar(presignedAvatarUrl);


        } catch (URISyntaxException e) {
            return userProfileDto.get();
        } catch (FeignException e) {
            // * there are some error when try to fetch data from other server
            e.printStackTrace();
            return userProfileDto.get();
            
        }


        return userProfileDto.get();

    }

    @DgsMutation
    public UserProfileDto updateUserProfile(@InputArgument ModifyUserProfileInput input, DgsDataFetchingEnvironment dfe)
            throws HttpResponseException {
        DgsRequestData requestData = dfe.getDgsContext().getRequestData();

        List<String> tokens = requestData.getHeaders().get("Authorization");

        AccessTokenDetailsDto userDetails = httpHeaderUtil.accessTokenVerification(tokens);
        userService.updateUserProfile(userDetails.getId(), input);

        Optional<UserProfileDto> userProfileDto = userService.getUserProfile(userDetails.getEmail());
        if (userProfileDto.isEmpty())
            throw new HttpNotFoundException();
        return userProfileDto.get();
    }
}
