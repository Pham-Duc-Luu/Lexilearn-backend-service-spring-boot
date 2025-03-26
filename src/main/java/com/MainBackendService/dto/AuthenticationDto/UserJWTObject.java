package com.MainBackendService.dto.AuthenticationDto;

import com.MainBackendService.exception.HttpInternalServerErrorException;
import com.MainBackendService.exception.HttpResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserJWTObject {
    private String user_email;
    private String user_name;
    private String user_id;
    private String user_uuid;

    public UserJWTObject(String user_email, String user_name, String user_id, String user_uuid) {
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_uuid = user_uuid;
    }

    public String toJson() throws HttpResponseException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this); // Convert object to JSON string
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new HttpInternalServerErrorException();
        }
    }
}
