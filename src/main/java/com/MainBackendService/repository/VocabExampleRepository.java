package com.MainBackendService.repository;

import com.MainBackendService.model.VocabExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabExampleRepository extends JpaRepository<VocabExample, Integer> {
}
