package MainBackendService.dto.Desk;

import com.jooq.sample.model.tables.records.DeskRecord;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateDeskDto {

    // Getters and setters
    @NotBlank(message = "Desk name is required")
    private String deskName;
    private String deskDescription;
    private String deskThumbnail;
    private String deskIcon;
    private Boolean deskIsPublic = false;
    private Integer deskOwnerId; // Assuming this is the user ID of the

    public CreateDeskDto() {
    }

    public CreateDeskDto(String deskName, String deskDescription, String deskThumbnail, String deskIcon, Boolean deskIsPublic, Integer deskOwnerId) {
        this.deskName = deskName;
        this.deskDescription = deskDescription;
        this.deskThumbnail = deskThumbnail;
        this.deskIcon = deskIcon;
        this.deskIsPublic = deskIsPublic;
        this.deskOwnerId = deskOwnerId;
    }

    public CreateDeskDto(DeskRecord deskRecord) {
        this.deskName = deskRecord.getDeskName();
        this.deskDescription = deskRecord.getDeskDescription();
        this.deskThumbnail = deskRecord.getDeskThumbnail();
        this.deskIcon = deskRecord.getDeskIcon();
        this.deskIsPublic = deskRecord.getDeskIsPublic() != null && deskRecord.getDeskIsPublic() == 1; // Convert Byte
        // to Boolean
        this.deskOwnerId = deskRecord.getDeskOwnerId(); // Assuming deskOwnerId is the user ID of the owner
    }

}
