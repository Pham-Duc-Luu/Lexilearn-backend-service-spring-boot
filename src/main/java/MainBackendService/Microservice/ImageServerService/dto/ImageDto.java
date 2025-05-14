package MainBackendService.Microservice.ImageServerService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ImageDto {
    private String _id;
    private String owner_UUID;
    private String url;
    private String width;
    private String height;
    private String description;
}