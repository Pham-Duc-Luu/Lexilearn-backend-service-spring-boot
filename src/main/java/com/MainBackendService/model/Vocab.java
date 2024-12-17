package com.MainBackendService.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Vocab")
public class Vocab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vocab_id;

    private String vocab_language;

    private String vocab_meaning;

    private String vocab_image;

    // Getters and Setters
}
