package com.MainBackendService.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DeskVocabFlashcardKey implements Serializable {

    private Integer DVF_desk_id;

    private Integer DVF_vocab_id;

    private Integer DVF_flashcard_id;

    // Default Constructor, Equals, and HashCode
}
