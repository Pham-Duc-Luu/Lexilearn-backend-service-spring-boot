package com.MainBackendService.dto.GraphqlDto;


import com.MainBackendService.modal.FlashcardModal;

import java.util.List;

public class FlashcardPaginationResult {
    private List<FlashcardModal> flashcards;
    private int total;
    private int skip;
    private int limit;

    public FlashcardPaginationResult(List<FlashcardModal> flashcards, int total, int skip, int limit) {
        this.flashcards = flashcards;
        this.total = total;
        this.skip = skip;
        this.limit = limit;
    }

    public List<FlashcardModal> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<FlashcardModal> flashcards) {
        this.flashcards = flashcards;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
