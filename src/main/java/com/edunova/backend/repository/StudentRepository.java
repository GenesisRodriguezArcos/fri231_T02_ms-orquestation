package com.edunova.backend.repository;

import com.edunova.backend.model.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {
    Flux<Student> findAllByOrderById();
    Flux<Student> findByStatusOrderById(String status);
    Mono<Student> findByCode(String code);
    Mono<Student> findByDni(String dni);
    Mono<Student> findByEmail(String email);
}
