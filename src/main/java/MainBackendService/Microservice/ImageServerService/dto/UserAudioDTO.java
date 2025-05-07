package MainBackendService.Microservice.ImageServerService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserAudioDTO {

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

    @JsonProperty("length_in_second")
    private Integer lengthInSecond;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("expire_at")
    private LocalDateTime expireAt;

    @JsonProperty("cloud_provider")
    private String cloudProvider;

}
