package MainBackendService.dto.Desk;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeskRequestBodyDto {

    @NotNull
    private Integer deskId;

    private UpdateDeskDto data;
}
