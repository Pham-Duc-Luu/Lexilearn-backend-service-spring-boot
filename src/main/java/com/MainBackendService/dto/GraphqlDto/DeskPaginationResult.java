package com.MainBackendService.dto.GraphqlDto;

import com.MainBackendService.modal.DeskModal;

import java.util.List;

public class DeskPaginationResult {
    //    @GraphQLQuery(name = "desks", description = "The list of desks")
    private final List<DeskModal> deskModals;
    //    @GraphQLQuery(name = "total", description = "The total number of desks")
    private final int total;

    //    @GraphQLQuery(name = "skip", description = "The number of skipped records")
    private final int skip;
    //    @GraphQLQuery(name = "limit", description = "The limit of records per page")
    private final int limit;

    public DeskPaginationResult(List<DeskModal> desks, int total, int skip, int limit) {
        this.deskModals = desks;
        this.total = total;
        this.skip = skip;
        this.limit = limit;
    }

    public List<DeskModal> getDesks() {
        return deskModals;
    }

    public int getTotal() {
        return total;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}