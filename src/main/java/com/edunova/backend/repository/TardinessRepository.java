package com.edunova.backend.repository;

import com.edunova.backend.model.Tardiness;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TardinessRepository extends ReactiveCrudRepository<Tardiness, Long> {
    Flux<Tardiness> findByStudentIdOrderByDateDesc(Long studentId);
    Flux<Tardiness> findAllByOrderByDateDesc();
}
