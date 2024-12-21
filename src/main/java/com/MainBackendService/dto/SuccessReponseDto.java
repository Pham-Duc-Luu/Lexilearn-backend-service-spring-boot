package com.MainBackendService.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class SuccessReponseDto<T> {
    private LocalDateTime timestamp;
    private int status;
    private T metadata;
    private String message;
    private String path;

    // Constructors
    public SuccessReponseDto(int status, T metadata, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.metadata = metadata;
        this.message = message;
        this.path = path;
    }

    // Constructors
    public SuccessReponseDto(T metadata) {
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.OK.value();
        this.metadata = metadata;
        this.message = HttpStatus.OK.getReasonPhrase();

    }


    // Constructors
    public SuccessReponseDto(int status, T metadata, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.metadata = metadata;
        this.message = message;
    }


    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getMetadata() {
        return metadata;
    }

    public void setMetadata(T metadata) {
        this.metadata = metadata;
    }
}
