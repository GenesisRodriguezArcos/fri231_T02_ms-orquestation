package com.edunova.backend.repository;

import com.edunova.backend.model.Warning;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface WarningRepository extends ReactiveCrudRepository<Warning, Long> {
    Flux<Warning> findByStudentIdOrderByDateDesc(Long studentId);
    Flux<Warning> findAllByOrderByDateDesc();
}
