package com.MainBackendService.service;

import com.MainBackendService.model.Desk;
import com.MainBackendService.model.DeskVocabFlashcard;
import com.MainBackendService.model.Vocab;
import com.MainBackendService.repository.DeskRepository;
import com.MainBackendService.repository.DeskVocabFlashcardRepository;
import com.MainBackendService.repository.VocabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeskVocabFlashcardService {

    @Autowired
    private VocabRepository vocabRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private DeskVocabFlashcardRepository deskVocabFlashcardRepository;

    public Vocab createAndAssociateVocabWithDesk(Integer deskId, Vocab vocab) {
        // Save the Vocab first
        Vocab savedVocab = vocabRepository.save(vocab);
        // Find the associated Desk
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new IllegalArgumentException("Desk not found with id: " + deskId));

        // Create and save the association
        DeskVocabFlashcard deskVocabFlashcard = new DeskVocabFlashcard();
        deskVocabFlashcard.setDesk(desk);
        deskVocabFlashcard.setVocab(vocab);
        deskVocabFlashcardRepository.save(deskVocabFlashcard);

        return savedVocab;
    }

    public DeskVocabFlashcard createAssociateVocabWithDesk(Integer vocabId, Integer deskId) {
        Desk desk = deskRepository.findById(deskId)
                .orElseThrow(() -> new IllegalArgumentException("Desk not found with id: " + deskId));

        Vocab vocab = vocabRepository.findById(vocabId)
                .orElseThrow(() -> new IllegalArgumentException("Desk not found with id: " + vocabId));
        // Create and save the association
        DeskVocabFlashcard deskVocabFlashcard = new DeskVocabFlashcard();
        deskVocabFlashcard.setDesk(desk);
        deskVocabFlashcard.setVocab(vocab);
        deskVocabFlashcardRepository.save(deskVocabFlashcard);
        return deskVocabFlashcard;
    }
}
