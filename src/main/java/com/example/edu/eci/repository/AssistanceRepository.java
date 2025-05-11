package com.example.edu.eci.repository;

import com.example.edu.eci.model.Assistance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssistanceRepository extends MongoRepository<Assistance, String> {
    boolean existsByUserIdAndClassId(String userId, String classId);
    long countByClassId(String claseId);
    Optional<Assistance> findByUserIdAndClassId(String usuarioId, String claseId);
    List<Assistance> findByConfirmFalseAndStartTimeIsAfter(LocalDateTime now);
    List<Assistance> findByConfirmTrue();
    void deleteByUserIdAndClassId(String userId, String classId);
    int countByUserIdAndConfirmTrueAndStartTimeBetween(String userId, LocalDate start, LocalDate end);
    int countByUserIdAndClassIdAndConfirmTrue(String userId, String classId);
    List<Assistance> findByConfirmFalseAndStartTimeIsBefore(LocalDateTime now);
}
