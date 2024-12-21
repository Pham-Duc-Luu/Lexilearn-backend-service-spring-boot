package com.MainBackendService.model;


import jakarta.persistence.*;

@Entity
public class VocabExample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VE_id")
    private Integer VEId;
    @Column(nullable = false, columnDefinition = "TEXT", name = "VE_text")
    private String VEText;
    @ManyToOne
    @JoinColumn(name = "VE_vocab_id", nullable = false)
    private Vocab vocab;

    public Integer getVEId() {
        return VEId;
    }

    public void setVEId(Integer VEId) {
        this.VEId = VEId;
    }

    public String getVEText() {
        return VEText;
    }

    public void setVEText(String VEText) {
        this.VEText = VEText;
    }

    public Vocab getVocab() {
        return vocab;
    }

    public void setVocab(Vocab vocab) {
        this.vocab = vocab;
    }


}
