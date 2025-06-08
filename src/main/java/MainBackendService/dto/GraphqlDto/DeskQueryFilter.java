package MainBackendService.dto.GraphqlDto;


import com.jooq.sample.model.enums.DeskDeskStatus;
import lombok.Data;

@Data
public class DeskQueryFilter {
    private Boolean isPublic;
    private DeskDeskStatus status;

 
}
