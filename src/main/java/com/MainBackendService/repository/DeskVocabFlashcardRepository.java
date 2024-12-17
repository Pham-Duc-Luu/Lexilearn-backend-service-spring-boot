package com.MainBackendService.repository;

import com.MainBackendService.model.DeskVocabFlashcard;
import com.MainBackendService.model.DeskVocabFlashcardKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeskVocabFlashcardRepository extends JpaRepository<DeskVocabFlashcard, DeskVocabFlashcardKey> {
}
