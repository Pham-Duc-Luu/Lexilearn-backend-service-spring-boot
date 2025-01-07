package com.MainBackendService.dto.createDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateVocabDto {
    @NotNull
    @NotBlank
    private String vocabLanguage;
    @NotBlank
    @NotNull
    private String vocabMeaning;
    private String vocabImage;

    @NotBlank
    @NotNull
    private String vocabText;

    public String getVocabLanguage() {
        return vocabLanguage;
    }

    public void setVocabLanguage(String vocabLanguage) {
        this.vocabLanguage = vocabLanguage;
    }

    public String getVocabMeaning() {
        return vocabMeaning;
    }

    public void setVocabMeaning(String vocabMeaning) {
        this.vocabMeaning = vocabMeaning;
    }

    public String getVocabImage() {
        return vocabImage;
    }

    public void setVocabImage(String vocabImage) {
        this.vocabImage = vocabImage;
    }

    public String getVocabText() {
        return vocabText;
    }

    public void setVocabText(String vocabText) {
        this.vocabText = vocabText;
    }

    // Getters and Setters
}
