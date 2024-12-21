package com.MainBackendService.dto;

public class DeskDto {
    private String deskId;
    private String deskName;
    private String deskDescription;
    private String deskThumbnail;
    private String deskIcon;
    private Boolean deskIsPublic;

    public DeskDto(String deskId, String deskName, String deskDescription, String deskThumbnail, String deskIcon, Boolean deskIsPublic) {
        this.deskId = deskId;
        this.deskName = deskName;
        this.deskDescription = deskDescription;
        this.deskThumbnail = deskThumbnail;
        this.deskIcon = deskIcon;
        this.deskIsPublic = deskIsPublic;
    }

    public DeskDto() {
    }

    public String getDeskId() {
        return deskId;
    }

    public void setDeskId(String deskId) {
        this.deskId = deskId;
    }

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
}
