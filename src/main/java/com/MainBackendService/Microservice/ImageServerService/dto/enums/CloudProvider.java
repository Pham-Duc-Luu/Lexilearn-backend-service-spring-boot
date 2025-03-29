package com.MainBackendService.Microservice.ImageServerService.dto.enums;

public enum CloudProvider {
    AMAZON_S3("AMAZON_S3"),
    GOOGLE_CLOUD("GOOGLE_CLOUD");

    private final String value;

    CloudProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
