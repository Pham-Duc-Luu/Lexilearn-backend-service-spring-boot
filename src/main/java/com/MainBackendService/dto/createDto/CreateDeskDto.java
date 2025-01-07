package com.MainBackendService.dto.createDto;

import jakarta.validation.constraints.NotBlank;

public class CreateDeskDto {

    @NotBlank(message = "Desk name is required")
    private String deskName;

    private String deskDescription;
    private String deskThumbnail;
    private String deskIcon;
    private Boolean deskIsPublic;
    private Integer deskOwnerId; // Assuming this is the user ID of the owner


    // Getters and setters
    public String getDeskName() {
        return deskName;
    }

    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }

    public String getDeskDescription() {
        return deskDescription;
    }

    public void setDeskDescription(String deskDescription) {
        this.deskDescription = deskDescription;
    }

    public String getDeskThumbnail() {
        return deskThumbnail;
    }

    public void setDeskThumbnail(String deskThumbnail) {
        this.deskThumbnail = deskThumbnail;
    }

    public String getDeskIcon() {
        return deskIcon;
    }

    public void setDeskIcon(String deskIcon) {
        this.deskIcon = deskIcon;
    }

    public Boolean getDeskIsPublic() {
        return deskIsPublic;
    }

    public void setDeskIsPublic(Boolean deskIsPublic) {
        this.deskIsPublic = deskIsPublic;
    }

    public Integer getDeskOwnerId() {
        return deskOwnerId;
    }

    public void setDeskOwnerId(Integer deskOwnerId) {
        this.deskOwnerId = deskOwnerId;
    }
}
