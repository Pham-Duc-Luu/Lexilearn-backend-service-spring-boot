package com.MainBackendService.repository;

import com.MainBackendService.model.Desk;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Integer> {
}
