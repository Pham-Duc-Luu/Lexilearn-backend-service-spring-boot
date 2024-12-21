package com.MainBackendService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.BadRequestException;

public class AccessTokenDetailsDto {
    @NotNull
    private final Integer id;
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String name;


    public AccessTokenDetailsDto(Integer id, String email, String name) throws BadRequestException {
        this.id = id;
        this.email = email;
        this.name = name;


    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
