package com.example.edu.eci.repository;

import com.example.edu.eci.model.Assistance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AssistanceRepository extends MongoRepository<Assistance, String> {
    boolean existsByUserIdAndClassId(String userId, String classId);
    long countByClassId(String claseId);
    Optional<Assistance> findByUserIdAndClassId(String usuarioId, String claseId);
}
