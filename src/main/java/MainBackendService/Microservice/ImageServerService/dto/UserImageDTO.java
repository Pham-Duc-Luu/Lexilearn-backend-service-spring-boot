package MainBackendService.Microservice.ImageServerService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserImageDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_uuid")
    private String userUUID;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("format")
    private String format;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("expire_at")
    private LocalDateTime expireAt;

    @JsonProperty("cloud_provider")
    private String cloudProvider;

    public UserImageDTO() {
    }

    public UserImageDTO(String id, String userUUID, String publicUrl, String fileName, Long fileSize, String format, Integer width, Integer height, LocalDateTime createdAt, LocalDateTime expireAt, String cloudProvider) {
        this.id = id;
        this.userUUID = userUUID;
        this.publicUrl = publicUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.format = format;
        this.width = width;
        this.height = height;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.cloudProvider = cloudProvider;
    }


}
