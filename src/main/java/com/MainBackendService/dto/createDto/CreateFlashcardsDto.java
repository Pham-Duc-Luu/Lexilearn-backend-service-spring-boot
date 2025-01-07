package com.MainBackendService.dto.createDto;

public class CreateFlashcardsDto {
    private CreateFlashcardDto[] flashcards;

    public CreateFlashcardsDto(CreateFlashcardDto[] flashcards) {
        this.flashcards = flashcards;
    }

    public CreateFlashcardDto[] getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(CreateFlashcardDto[] flashcards) {
        this.flashcards = flashcards;
    }
}
