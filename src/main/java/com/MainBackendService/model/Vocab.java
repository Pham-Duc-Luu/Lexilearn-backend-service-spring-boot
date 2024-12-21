package com.MainBackendService.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Vocab")
public class Vocab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocab_id")
    private Integer vocabId;
    @Enumerated(EnumType.STRING) // Optionally use EnumType.ORDINAL
    @Column(name = "vocab_language")
    private VocabLanguageType vocabLanguage;
    @Column(name = "vocab_meaning")
    private String vocabMeaning;
    @Column(name = "vocab_image")
    private String vocabImage;
    @Column(name = "vocab_text")
    private String vocabText;
    @OneToMany(mappedBy = "vocab", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabExample> examples;

    public Integer getVocabId() {
        return vocabId;
    }

    public void setVocabId(Integer vocabId) {
        this.vocabId = vocabId;
    }

    public VocabLanguageType getVocabLanguage() {
        return vocabLanguage;
    }

    public void setVocabLanguage(VocabLanguageType vocabLanguage) {
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

    public List<VocabExample> getExamples() {
        return examples;
    }

    public void setExamples(List<VocabExample> examples) {
        this.examples = examples;
    }

}
