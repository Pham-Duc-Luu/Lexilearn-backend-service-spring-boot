package com.MainBackendService.modal;

import com.MainBackendService.dto.DeskDto;
import com.MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import com.jooq.sample.model.enums.DeskDeskStatus;
import com.jooq.sample.model.tables.records.DeskRecord;

import java.util.List;

public class DeskModal {
    private final String thumbnail;
    //    @GraphQLQuery(name = "id")
    private String id;
    //    @GraphQLQuery(name = "name")
    private String name;
    //    @GraphQLQuery(name = "description")
    private String description;
    //    @GraphQLQuery(name = "icon")
    private String icon;
    //    @GraphQLQuery(name = "isPublic")
    private Boolean isPublic;
    //    @GraphQLQuery(name = "ownerId")
    private String ownerId;
    private UserModal owner;
    private String createdAt;
    private String updatedAt;
    private DeskDeskStatus status;
    private FlashcardPaginationResult flashcardPaginationResult;
    private List<FlashcardModal> flashcards;

    public DeskModal(DeskDto deskDto) {
        this.id = deskDto.getDeskId();
        this.name = deskDto.getDeskName();
        this.description = deskDto.getDeskDescription();
        this.icon = deskDto.getDeskIcon();
        this.isPublic = deskDto.getDeskIsPublic();
        this.ownerId = String.valueOf(deskDto.getDeskOwnerId()); // Ensure type consistency
        this.thumbnail = deskDto.getDeskThumbnail();
        this.createdAt = deskDto.getCreatedAt();
        this.updatedAt = deskDto.getUpdatedAt();
        this.status = DeskDeskStatus.valueOf(deskDto.getDeskStatus());

    }

    public DeskModal(DeskRecord deskRecord) {
        this.id = String.valueOf(deskRecord.getDeskId());
        this.name = deskRecord.getDeskName();
        this.description = deskRecord.getDeskDescription();
        this.icon = deskRecord.getDeskIcon();
        this.isPublic = deskRecord.getDeskIsPublic() == 1;
        this.ownerId = String.valueOf(deskRecord.getDeskOwnerId()); // Ensure type consistency
        this.thumbnail = deskRecord.getDeskThumbnail();
        this.createdAt = String.valueOf(deskRecord.getCreatedAt());
        this.updatedAt = String.valueOf(deskRecord.getUpdatedAt());
        this.status = DeskDeskStatus.valueOf(deskRecord.getDeskStatus().toString());
    }

    public DeskModal(String id, String name, String description, String icon, Boolean isPublic, String ownerId, String thumbnail, String createdAt, String updatedAt, DeskDeskStatus deskStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
        this.thumbnail = thumbnail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = deskStatus;
    }

    public DeskModal(String id, String name, String description, String icon, Boolean isPublic, String ownerId, String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
        this.thumbnail = thumbnail;
    }

    public List<FlashcardModal> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashcardModal> flashcards) {
        this.flashcards = flashcards;
    }

    public DeskDeskStatus getStatus() {
        return status;
    }

    public void setStatus(DeskDeskStatus status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public FlashcardPaginationResult getFlashcardPaginationResult() {
        return flashcardPaginationResult;
    }

    public void setFlashcardPaginationResult(FlashcardPaginationResult flashcardPaginationResult) {
        this.flashcardPaginationResult = flashcardPaginationResult;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public UserModal getOwner() {
        return owner;
    }

    public void setOwner(UserModal owner) {
        this.owner = owner;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
