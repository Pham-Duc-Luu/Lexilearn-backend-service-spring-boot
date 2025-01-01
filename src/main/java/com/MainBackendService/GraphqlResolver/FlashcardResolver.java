package com.MainBackendService.GraphqlResolver;

import com.MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import com.MainBackendService.service.FlashcardService.FlashcardGQLService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DgsComponent
public class FlashcardResolver {
    Logger logger = LogManager.getLogger(FlashcardResolver.class);

    @Autowired
    private FlashcardGQLService flashcardGQLService;


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


}
