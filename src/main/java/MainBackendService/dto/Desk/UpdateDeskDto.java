package MainBackendService.dto.Desk;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeskDto {
    @NotNull
    private Integer desk_id;

    private String desk_description;
    private String desk_thumbnail;
    private String desk_icon;
    private Boolean desk_is_public;
    private String desk_name;
    private String desk_status;

}
