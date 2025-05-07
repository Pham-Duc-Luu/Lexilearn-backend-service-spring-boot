package MainBackendService.dto.GraphqlDto;

import lombok.Data;

@Data
public class UpdateDeskInput {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String isPublic;
    private String thumbnail;
    private String status;
}
