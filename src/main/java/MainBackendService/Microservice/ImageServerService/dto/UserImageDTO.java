package MainBackendService.Microservice.ImageServerService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

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

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }
}
