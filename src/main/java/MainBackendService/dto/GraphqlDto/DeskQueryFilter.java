package MainBackendService.dto.GraphqlDto;


import com.jooq.sample.model.enums.DeskDeskStatus;
import lombok.Data;

@Data
public class DeskQueryFilter {
    private Boolean isPublic;
    private DeskDeskStatus status;

    public DeskDeskStatus getStatus() {
        return status;
    }

    public void setStatus(DeskDeskStatus status) {
        this.status = status;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }
}
