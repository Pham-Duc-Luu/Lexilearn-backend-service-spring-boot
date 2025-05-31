package MainBackendService.dto.GraphqlDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateFlashcardInput {
    private Integer id;
    private Integer desk_id;
    private String front_image;
    private String front_text;
    private String front_sound;
    private String back_image;
    private String back_text;
    private String back_sound;

   
}
