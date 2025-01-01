package com.MainBackendService.modal;

public class DeskModal {
    //    @GraphQLQuery(name = "id")
    private String id;
    //    @GraphQLQuery(name = "name")
    private String name;
    //    @GraphQLQuery(name = "description")
    private String description;
    //    @GraphQLQuery(name = "icon")
    private String icon;
    //    @GraphQLQuery(name = "isPublic")
    private Boolean isPublic;
    //    @GraphQLQuery(name = "ownerId")
    private String ownerId;
    private UserModal owner;


    public DeskModal(String id, String name, String description, String icon, Boolean isPublic, String ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isPublic = isPublic;
        this.ownerId = ownerId;
    }

    public UserModal getOwner() {
        return owner;
    }

    public void setOwner(UserModal owner) {
        this.owner = owner;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
