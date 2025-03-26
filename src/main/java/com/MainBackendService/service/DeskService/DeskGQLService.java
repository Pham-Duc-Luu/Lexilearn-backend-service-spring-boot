package com.MainBackendService.service.DeskService;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.MainBackendService.dto.DeskDto;
import com.MainBackendService.dto.GraphqlDto.DeskPaginationResult;
import com.MainBackendService.dto.GraphqlDto.DeskQueryFilter;
import com.MainBackendService.dto.GraphqlDto.DeskQuerySort;
import com.MainBackendService.dto.GraphqlDto.SearchDeskArg;
import com.MainBackendService.modal.DeskModal;
import com.MainBackendService.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.jooq.sample.model.tables.Desk.DESK;


@Service
public class DeskGQLService {
    private final UserService userService;
    private final Integer maximumQueryRecord = 100;
    Logger logger = LogManager.getLogger(DeskGQLService.class);
    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public DeskGQLService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches a list of desks with pagination details.
     *
     * @param skip  The offset for pagination, default to 0.
     * @param limit The number of items per page, default to 30.
     * @return DeskPaginationResult containing the list of desks and pagination details.
     */

//    @GraphQLQuery(name = "getDesks")
    public DeskPaginationResult getDesks(Integer skip, Integer limit) {
        // define a query condition

        // Set default values for skip and limit if not provided
        if (skip == null) {
            skip = 0;
        }
        if (limit == null) {
            limit = 30;
        }

        if (limit > maximumQueryRecord) limit = maximumQueryRecord;
        // Fetch the total number of desks
        int total = dslContext.selectCount()
                .from(DESK)
                .fetchOne(0, int.class); // Get the total count

        // Fetch the list of desks with pagination (skip/limit)
        List<DeskModal> desks = dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID, DESK.DESK_THUMBNAIL)
                .from(DESK)
                .limit(limit)
                .offset(skip).fetchInto(DeskModal.class);
        // Map to DeskModal

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }

    public DeskPaginationResult getDesks(Integer skip, Integer limit, DeskQuerySort deskQuerySort, DeskQueryFilter deskQueryFilter) {
        // define a query condition
        // Set default values for skip and limit if not provided
        if (skip == null) skip = 0;
        if (limit == null) limit = 30;
        if (limit > maximumQueryRecord) limit = maximumQueryRecord;

        // Start building the query
        SelectConditionStep query = dslContext.select(
                        DESK.DESK_ID,
                        DESK.DESK_NAME,
                        DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON,
                        DESK.DESK_IS_PUBLIC,
                        DESK.DESK_OWNER_ID,
                        DESK.DESK_THUMBNAIL,
                        DESK.CREATED_AT,
                        DESK.UPDATED_AT,
                        DESK.DESK_STATUS)
                .from(DESK)
                .where(DSL.trueCondition()); // Start with a true condition for dynamic filtering

        // ✅ Apply filtering based on `deskQueryFilter`
//        if (deskQueryFilter != null) {
//            if (deskQueryFilter.getIsPublic() != null) {
//                query.and(DESK.DESK_IS_PUBLIC.eq((byte) (deskQueryFilter.getIsPublic() ? 1 : 0)));
//            }
//            if (deskQueryFilter.getDeskStatus() != null) {
////                switch (deskQueryFilter.getDeskStatus()) {
////                    case
////                }
//                query.and(DESK.DESK_STATUS.eq(deskQueryFilter.getDeskStatus())); // Enum to string conversion
//            }
//        }

        //✅ Apply sorting based on `deskQuerySort`
        if (deskQuerySort != null && deskQuerySort.getDeskKey() != null && deskQuerySort.getOrder() != null) {
            SortField<?> sortField = deskQuerySort.getOrder().equals(
                    SortOrder.ASC
            )
                    ? deskQuerySort.getDeskKey().asc()
                    : deskQuerySort.getDeskKey().desc();
            query.orderBy(sortField);
        }

        // ✅ Fetch total count before applying pagination
        int total = dslContext.fetchCount(query);

        // ✅ Apply pagination
        List<DeskModal> desks = query
                .limit(limit)
                .offset(skip)
                .fetchInto(DeskModal.class);

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }


    public DeskModal getDesk(Integer id) {
        return dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_ID.eq(id))
                .fetchOneInto(DeskModal.class);
    }

    public DeskPaginationResult getPublicDesk(Integer skip, Integer limit) {
        // define a query condition
        Condition conditionQuery = DESK.DESK_IS_PUBLIC.eq((byte) 1);
        // Set default values for skip and limit if not provided
        if (skip == null) {
            skip = 0;
        }
        if (limit == null) {
            limit = 30;
        }
        // Fetch the total number of desks
        int total = dslContext.selectCount()
                .from(DESK)
                .where(conditionQuery)
                .fetchOne(0, int.class); // Get the total count

        // Fetch the list of desks with pagination (skip/limit)
        List<DeskModal> desks = dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(conditionQuery)
                .offset(skip).fetchInto(DeskModal.class);
        // Map to DeskModal

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }

    public DeskModal getUserDesk(Integer userId, Integer deskId) {
        return dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(DESK.DESK_ID.eq(deskId).and(DESK.DESK_OWNER_ID.eq(userId)))
                .fetchOneInto(DeskModal.class);
    }

    public DeskPaginationResult getDeskByUserId(Integer id, Integer skip, Integer limit) {

        Condition conditionQuery = DESK.DESK_IS_PUBLIC.eq((byte) 1).and(
                DESK.DESK_OWNER_ID.eq(id));

        // Set default values for skip and limit if not provided
        if (skip == null) {
            skip = 0;
        }
        if (limit == null) {
            limit = 30;
        }

        if (limit > maximumQueryRecord) limit = maximumQueryRecord;

        // * set the maximum number of record is
        // Fetch the total number of desks
        int total = dslContext.selectCount()
                .from(DESK)
                .where(conditionQuery)
                .fetchOne(0, int.class); // Get the total count

        // Fetch the list of desks with pagination (skip/limit)
        List<DeskModal> desks = dslContext.select(DESK.DESK_ID, DESK.DESK_NAME, DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON, DESK.DESK_IS_PUBLIC, DESK.DESK_OWNER_ID)
                .from(DESK)
                .where(conditionQuery) // Explicitly cast 1 to byte
                .limit(limit)
                .offset(skip).fetchInto(DeskModal.class);
        // Map to DeskModal

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }

    public DeskPaginationResult getDeskByUserId(Integer id, Integer skip, Integer limit, DeskQuerySort deskQuerySort, DeskQueryFilter deskQueryFilter) {
        // define a query condition
        // Set default values for skip and limit if not provided
        if (skip == null) skip = 0;
        if (limit == null) limit = 30;
        if (limit > maximumQueryRecord) limit = maximumQueryRecord;

        // Start building the query
        SelectConditionStep query = dslContext.select(
                        DESK.DESK_ID,
                        DESK.DESK_NAME,
                        DESK.DESK_DESCRIPTION,
                        DESK.DESK_ICON,
                        DESK.DESK_IS_PUBLIC,
                        DESK.DESK_OWNER_ID,
                        DESK.DESK_THUMBNAIL,
                        DESK.CREATED_AT,
                        DESK.UPDATED_AT,
                        DESK.DESK_STATUS)
                .from(DESK)
                .where(DSL.trueCondition()); // Start with a true condition for dynamic filtering

        // select only desk belong to user's id
        query.and(DESK.DESK_OWNER_ID.eq(id));

        // ✅ Apply filtering based on `deskQueryFilter`
        if (deskQueryFilter != null) {
            if (deskQueryFilter.getIsPublic() != null) {
                query.and(DESK.DESK_IS_PUBLIC.eq((byte) (deskQueryFilter.getIsPublic() ? 1 : 0)));
            }
            if (deskQueryFilter.getStatus() != null) {
//                switch (deskQueryFilter.getDeskStatus()) {
//                    case
//                }
                query.and(DESK.DESK_STATUS.eq(deskQueryFilter.getStatus())); // Enum to string conversion
            }
        }

        //✅ Apply sorting based on `deskQuerySort`
        if (deskQuerySort != null && deskQuerySort.getDeskKey() != null && deskQuerySort.getOrder() != null) {
            SortField<?> sortField = deskQuerySort.getOrder().equals(
                    SortOrder.ASC
            )
                    ? deskQuerySort.getDeskKey().asc()
                    : deskQuerySort.getDeskKey().desc();
            query.orderBy(sortField);
        }

        // ✅ Fetch total count before applying pagination
        int total = dslContext.fetchCount(query);

        // ✅ Apply pagination
        List<DeskModal> desks = query
                .limit(limit)
                .offset(skip)
                .fetchInto(DeskModal.class);

        // Return the result wrapped in DeskPaginationResult
        return new DeskPaginationResult(desks, total, skip, limit);
    }

    public List<DeskDto> searchUserDesksByText(SearchDeskArg searchDeskArg, Integer owerID) {

        Query searchQuery;

        // * apply ramdom search

        searchQuery = Query.of(q -> q
                .bool(b -> b
                        .should(sh -> sh
                                .match(f -> f
                                        .query(searchDeskArg.getQ())
                                        .field(DeskDto.DESK_DESCRIPTION)

                                        .fuzziness("AUTO")
                                )
                        )
                        .should(sh -> sh
                                .match(f -> f
                                        .field(DeskDto.DESK_NAME)
                                        .query(searchDeskArg.getQ())

                                        .fuzziness("AUTO")
                                )
                        ).filter(f -> f.term(t -> t.field(DeskDto.DESK_OWNER_ID).value(owerID)))
                        .minimumShouldMatch("1")
                )

        );


        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .build();

        SearchHits<DeskDto> searchHits = elasticsearchOperations.search(nativeQuery, DeskDto.class, DeskDto.DEFAULT_INDEX_COORDINATES);


        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<DeskDto> searchUserDesksByText(Integer skip, Integer limit, SearchDeskArg searchDeskArg, Integer owerID) {

        Query searchQuery;

        // * apply ramdom search

        searchQuery = Query.of(q -> q
                .bool(b -> b
                        .should(sh -> sh
                                .match(f -> f
                                        .query(searchDeskArg.getQ())
                                        .field(DeskDto.DESK_DESCRIPTION)

                                        .fuzziness("AUTO")
                                )
                        )
                        .should(sh -> sh
                                .match(f -> f
                                        .field(DeskDto.DESK_NAME)
                                        .query(searchDeskArg.getQ())

                                        .fuzziness("AUTO")
                                )
                        ).filter(f -> f.term(t -> t.field(DeskDto.DESK_OWNER_ID).value(owerID)))
                        .minimumShouldMatch("1")
                )

        );


        Pageable pageable = PageRequest.of(skip / limit, limit);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .withPageable(pageable)
                .build();

        SearchHits<DeskDto> searchHits = elasticsearchOperations.search(nativeQuery, DeskDto.class, DeskDto.DEFAULT_INDEX_COORDINATES);


        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<DeskDto> searchDesksByText(Integer skip, Integer limit, SearchDeskArg searchDeskArg) {

        Query searchQuery;


        // * apply ramdom search
        // TODO : Remove or update this ramdom
        if (searchDeskArg == null || searchDeskArg.getQ() == null || searchDeskArg.getQ().isEmpty() || searchDeskArg.getIsRandom()) {
            searchQuery = Query.of(q ->
                    q.functionScore(f -> f
                            .functions(fn -> fn
                                    .randomScore(
                                            r -> r.seed(searchDeskArg.getRandomScore())
                                    ) // Generates a different result each time
                            )));
        } else {
            searchQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(sh -> sh
                                    .match(f -> f
                                            .query(searchDeskArg.getQ())
                                            .field(DeskDto.DESK_DESCRIPTION)

                                            .fuzziness("AUTO")
                                    )
                            )
                            .should(sh -> sh
                                    .match(f -> f
                                            .field(DeskDto.DESK_NAME)
                                            .query(searchDeskArg.getQ())

                                            .fuzziness("AUTO")
                                    )
                            )
                            .minimumShouldMatch("1")
                    )
            );

        }

        Pageable pageable = PageRequest.of(skip / limit, limit);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .withPageable(pageable)
                .build();

        SearchHits<DeskDto> searchHits = elasticsearchOperations.search(nativeQuery, DeskDto.class, DeskDto.DEFAULT_INDEX_COORDINATES);


        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
