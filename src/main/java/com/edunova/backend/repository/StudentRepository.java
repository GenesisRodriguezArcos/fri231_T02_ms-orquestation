package com.edunova.backend.repository;

import com.edunova.backend.model.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {
    Flux<Student> findAllByOrderById();
    Flux<Student> findByStatusOrderById(String status);
}
