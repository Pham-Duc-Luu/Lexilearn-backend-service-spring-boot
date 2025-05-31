package MainBackendService.dto.Flashcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Validated
public class UpsertFlashcardRequestBodyDto {

    @Valid
    private List<UpsertFlashcardDto> data;

    @NotNull(message = "desk's id in not exist!")
    private Integer deskId;

    public boolean hasUniquePositions() {
        Set<Integer> positions = new HashSet<>();
        for (UpsertFlashcardDto dto : data) {
            if (!positions.add(dto.getPosition())) {
                return false; // duplicate position found
            }
        }
        return true; // all positions are unique
    }

    public boolean hasRightOperationType() {
        for (UpsertFlashcardDto upsertFlashcardDto : data) {
            if (!upsertFlashcardDto.isValidOperation()) {
                return false;
            }
        }
        return true;
    }
}

