package com.MainBackendService.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Flashcard")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flashcard_id;

    private String flashcard_front_image;

    private String flashcard_front_sound;

    private String flashcard_front_text;

    private String flashcard_back_image;

    private String flashcard_back_sound;

    private String flashcard_back_text;

    // Getters and Setters
}
