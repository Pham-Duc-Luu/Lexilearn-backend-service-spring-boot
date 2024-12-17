package com.MainBackendService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "User_Token")
public class UserToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="UT_id")
    private Integer UTId;

    @Enumerated(EnumType.STRING) // Optionally use EnumType.ORDINAL
    @Column(name ="UT_type", nullable = false)
    private UserTokenType UTType;

    @Column(columnDefinition = "TIMESTAMP", name = "UT_expired_at")
    private LocalDateTime UTExpiredAt;

    @ManyToOne
    @JoinColumn(name = "UT_user_id", nullable = false)
    private User user;


    @Column(nullable = false, name = "UT_text")
    private String UTText;

    public String getUTText() {
        return UTText;
    }

    public void setUTText(String UTText) {
        this.UTText = UTText;
    }

    public Integer getUTId() {
        return UTId;
    }

    public void setUTId(Integer UTId) {
        this.UTId = UTId;
    }

    public UserTokenType getUTType() {
        return UTType;
    }

    public void setUTType(UserTokenType UTType) {
        this.UTType = UTType;
    }

    public LocalDateTime getUTExpiredAt() {
        return UTExpiredAt;
    }

    public void setUTExpiredAt(LocalDateTime UTExpiredAt) {
        this.UTExpiredAt = UTExpiredAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
