package com.MainBackendService.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "User")
public class User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, name = "user_name")
    private String userName;



    @Column(nullable = false, name = "user_email")
    private String userEmail;

    @Column(nullable = false, name ="user_password")
    private String userPassword;

    @Enumerated(EnumType.STRING) // Optionally use EnumType.ORDINAL
    @Column(name ="user_provider")
    private UserProvider userProvider;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name="created_at")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", name = "update_at")
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserToken> tokens;

    @OneToMany(mappedBy = "deskOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Desk> desks;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }


    public void setUserProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }



    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public List<UserToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<UserToken> tokens) {
        this.tokens = tokens;
    }

    public List<Desk> getDesks() {
        return desks;
    }

    public void setDesks(List<Desk> desks) {
        this.desks = desks;
    }

}
