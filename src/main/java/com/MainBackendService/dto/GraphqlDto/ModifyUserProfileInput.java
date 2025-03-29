package com.MainBackendService.dto.GraphqlDto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserProfileInput extends AvatarDto {
    @NotNull
    @Size(min = 8, max = 30, message = "Name must be between 8 and 30 characters")
    private String name;
    private String avatar;


}
