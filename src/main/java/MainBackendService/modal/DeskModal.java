package MainBackendService.modal;

import MainBackendService.dto.DeskDto;
import MainBackendService.dto.GraphqlDto.FlashcardPaginationResult;
import com.jooq.sample.model.enums.DeskDeskStatus;
import com.jooq.sample.model.tables.records.DeskRecord;
import lombok.Data;

import java.util.List;

@Data
public class DeskModal {
    private final String thumbnail;
    private String id;
    private String name;
    private String description;
    private String icon;
    private Boolean isPublic;
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

}
