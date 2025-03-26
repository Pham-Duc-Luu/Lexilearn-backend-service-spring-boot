package com.MainBackendService.dto.GraphqlDto;

import lombok.Data;

@Data
public class SearchDeskArg {
    private String q;
    private Boolean isRandom = false;
    private String randomScore = String.valueOf(System.currentTimeMillis());
}
