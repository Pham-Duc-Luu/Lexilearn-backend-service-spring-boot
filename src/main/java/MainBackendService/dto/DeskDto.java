package MainBackendService.dto;

import MainBackendService.dto.GraphqlDto.UpdateDeskInput;
import MainBackendService.modal.DeskModal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

@Data
public class DeskDto {

    public final static String DESK_ID = "desk_id";
    public final static String DESK_DESCRIPTION = "desk_description";
    public final static String DESK_THUMBNAIL = "desk_thumbnail";
    public final static String DESK_ICON = "desk_icon";
    public final static String DESK_IS_PUBLIC = "desk_is_public";
    public final static String DESK_OWNER_ID = "desk_owner_id";
    public final static String DESK_NAME = "desk_name";
    public final static String CREATED_AT = "created_at";
    public final static String UPDATED_AT = "updated_at";
    public final static String DESK_STATUS = "desk_status";
    public final static IndexCoordinates DEFAULT_INDEX_COORDINATES = IndexCoordinates.of("desks");

    @Value(value = "${kafka.es.index.desk.name}")
    private String deskIndexName = "desks";
    private IndexCoordinates indexCoordinates = IndexCoordinates.of(deskIndexName);
    @Id
    @JsonProperty("desk_id")
    @Field(name = "desk_id")
    private String deskId;
    @JsonProperty("desk_description")
    @Field(name = "desk_description")
    private String deskDescription;
    @JsonProperty("desk_thumbnail")
    @Field(name = "desk_thumbnail")
    private String deskThumbnail;
    @JsonProperty("desk_icon")
    @Field(name = "desk_icon")
    private String deskIcon;
    @JsonProperty("desk_is_public")
    @Field(name = "desk_is_public")
    private Boolean deskIsPublic;
    @JsonProperty("desk_owner_id")
    @Field(name = "desk_owner_id")
    private int deskOwnerId;
    @JsonProperty("desk_name")
    @Field(name = "desk_name")
    private String deskName;
    @JsonProperty("created_at")
    @Field(name = "created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    @Field(name = "updated_at")
    private String updatedAt;
    @JsonProperty("desk_status")
    @Field(name = "desk_status")
    private String deskStatus;


    public DeskDto(String deskId, String deskName, String deskDescription, String deskThumbnail, String deskIcon, Boolean deskIsPublic) {
        this.deskId = deskId;
        this.deskName = deskName;
        this.deskDescription = deskDescription;
        this.deskThumbnail = deskThumbnail;
        this.deskIcon = deskIcon;
        this.deskIsPublic = deskIsPublic;
        this.indexCoordinates = IndexCoordinates.of(deskIndexName);
    }

    public DeskDto() {
    }

    public DeskDto(UpdateDeskInput updateDeskInput) {
        this.deskId = updateDeskInput.getId();
        this.deskName = updateDeskInput.getName();
        this.deskDescription = updateDeskInput.getDescription();
        this.deskThumbnail = updateDeskInput.getThumbnail();
        this.deskIcon = updateDeskInput.getIcon();
        this.deskIsPublic = Boolean.valueOf(updateDeskInput.getIsPublic());
        this.deskStatus = String.valueOf(updateDeskInput.getStatus());
        this.indexCoordinates = IndexCoordinates.of(deskIndexName);
    }

    public DeskDto(DeskModal deskModal) {
        this.deskId = deskModal.getId();
        this.deskName = deskModal.getName();
        this.deskDescription = deskModal.getDescription();
        this.deskThumbnail = deskModal.getThumbnail();
        this.deskIcon = deskModal.getIcon();
        this.deskIsPublic = deskModal.getIsPublic();
        this.deskOwnerId = Integer.parseInt(deskModal.getOwnerId());
        this.deskStatus = String.valueOf(deskModal.getStatus());

        this.indexCoordinates = IndexCoordinates.of(deskIndexName);
    }


}
