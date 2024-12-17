package com.MainBackendService.repository;

import com.MainBackendService.model.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabRepository extends JpaRepository<Vocab, Integer> {
}
