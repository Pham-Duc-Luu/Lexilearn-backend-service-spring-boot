package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.Microservice.ImageServerService.service.ImageServerClient;
import com.MainBackendService.Microservice.ImageServerService.service.MediaService;
import com.MainBackendService.dto.AccessTokenDetailsDto;
import com.MainBackendService.dto.GraphqlDto.AvatarDto;
import com.MainBackendService.dto.GraphqlDto.ModifyUserProfileInput;
import com.MainBackendService.dto.UserProfileDto;
import com.MainBackendService.exception.HttpNotFoundException;
import com.MainBackendService.exception.HttpResponseException;
import com.MainBackendService.service.AccessTokenJwtService;
import com.MainBackendService.service.DeskService.DeskGQLService;
import com.MainBackendService.service.DeskService.DeskService;
import com.MainBackendService.service.FlashcardService.FlashcardService;
import com.MainBackendService.service.UserService.UserService;
import com.MainBackendService.utils.HttpHeaderUtil;
import com.jooq.sample.model.tables.records.UseravatarRecord;
import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    private MediaService imageService;

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

        try {
            URL url = new URL(userProfileDto.get().getAvatar());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // HEAD request to check without downloading
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                // * image url still valid
                return userProfileDto.get();

            } else {
                // Extract the path from the URL
                try {
                    URI uri = new URI(userProfileDto.get().getAvatar());
                    String path = uri.getPath();

                    // Extract filename (last part of the path)
                    String fileName = path.substring(path.lastIndexOf("/") + 1);

                    return userService.updateUserAvatar(userDetails.getId(),
                            imageServerClient.getUserImage(tokens.getFirst(), fileName).getPublicUrl());

                } catch (URISyntaxException e) {
                    return userProfileDto.get();
                }
            }

        } catch (Exception e) {
            // * image url still valid
            return userProfileDto.get();
        }

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
