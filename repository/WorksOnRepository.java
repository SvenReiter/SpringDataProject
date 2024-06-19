package org.example.repository;

import org.example.model.WorksOn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorksOnRepository extends JpaRepository<WorksOn, Long> {
    void deleteById(Long id);
}
