package MainBackendService.dto.createDto;

import java.util.List;

public class CreateFlashcardsDto {
    private CreateFlashcardDto[] flashcards;

    public CreateFlashcardsDto(CreateFlashcardDto[] flashcards) {
        this.flashcards = flashcards;
    }

    public CreateFlashcardsDto(List<CreateFlashcardDto> flashcards) {
        this.flashcards = flashcards.stream().toArray(CreateFlashcardDto[]::new);
    }

    public CreateFlashcardDto[] getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(CreateFlashcardDto[] flashcards) {
        this.flashcards = flashcards;
    }
}
