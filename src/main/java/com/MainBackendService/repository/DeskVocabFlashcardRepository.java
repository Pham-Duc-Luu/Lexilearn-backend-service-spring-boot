package com.MainBackendService.repository;

import com.MainBackendService.model.DeskVocabFlashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeskVocabFlashcardRepository extends JpaRepository<DeskVocabFlashcard, Integer> {
}
