package com.MainBackendService.model;
import jakarta.persistence.*;

@Entity
@Table(name = "Desk")
public class Desk {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desk_id")
    private Integer deskId;


    @Column(name = "desk_description")
    private String deskDescription;

    @Column(name = "desk_thumbnail")
    private String deskThumbnail;

    @Column(name = "desk_icon")
    private String deskIcon;

    @Column(name = "desk_is_public")
    private Boolean deskIsPublic;

    @ManyToOne
    @JoinColumn(name = "desk_owner_id", nullable = false)
    private User deskOwner;

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }
    public String getDeskDescription() {
        return deskDescription;
    }

    public void setDeskDescription(String deskDescription) {
        this.deskDescription = deskDescription;
    }

    public String getDeskIcon() {
        return deskIcon;
    }

    public void setDeskIcon(String deskIcon) {
        this.deskIcon = deskIcon;
    }

    public Boolean getDeskIsPublic() {
        return deskIsPublic;
    }

    public void setDeskIsPublic(Boolean deskIsPublic) {
        this.deskIsPublic = deskIsPublic;
    }
}
