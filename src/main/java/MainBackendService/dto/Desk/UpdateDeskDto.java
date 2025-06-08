package MainBackendService.dto.Desk;

import lombok.Data;

@Data
public class UpdateDeskDto {

    private String desk_description;
    private String desk_thumbnail;
    private String desk_icon;
    private Boolean desk_is_public;
    private String desk_name;
    private String desk_status;

}
