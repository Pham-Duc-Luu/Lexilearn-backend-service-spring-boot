package com.MainBackendService.model;

public enum VocabLanguageType {
    JAPANESE("JAPANESE"),
    CHINESE("CHINESE"),
    ENGLISH("ENGLISH");

    private final String type;

    VocabLanguageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
