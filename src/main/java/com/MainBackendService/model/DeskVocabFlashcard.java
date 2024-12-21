package com.MainBackendService.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Desk_Vocab_Flashcard")
public class DeskVocabFlashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DVF_id")
    private Integer DVFId;

    @ManyToOne
    @JoinColumn(name = "DVF_desk_id")
    private Desk desk;
    @ManyToOne
    @JoinColumn(name = "DVF_vocab_id", nullable = true)
    private Vocab vocab;
    @ManyToOne
    @JoinColumn(name = "DVF_flashcard_id", nullable = true)
    private Flashcard flashcard;

    public Integer getDVFId() {
        return DVFId;
    }

    public void setDVFId(Integer DVFId) {
        this.DVFId = DVFId;
    }

    public Desk getDesk() {
        return desk;
    }

    public void setDesk(Desk desk) {
        this.desk = desk;
    }

    public Vocab getVocab() {
        return vocab;
    }

    public void setVocab(Vocab vocab) {
        this.vocab = vocab;
    }

    public Flashcard getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }

}
