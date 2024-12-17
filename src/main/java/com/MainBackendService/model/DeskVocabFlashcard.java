package com.MainBackendService.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Desk_Vocab_Flashcard")
public class DeskVocabFlashcard {

    @EmbeddedId
    private DeskVocabFlashcardKey id;

    @ManyToOne
    @MapsId("DVF_desk_id")
    @JoinColumn(name = "DVF_desk_id")
    private Desk desk;

    @ManyToOne
    @MapsId("DVF_vocab_id")
    @JoinColumn(name = "DVF_vocab_id")
    private Vocab vocab;

    @ManyToOne
    @MapsId("DVF_flashcard_id")
    @JoinColumn(name = "DVF_flashcard_id")
    private Flashcard flashcard;

    // Getters and Setters
}
