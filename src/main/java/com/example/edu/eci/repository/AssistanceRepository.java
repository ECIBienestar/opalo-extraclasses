package com.example.edu.eci.repository;

import com.example.edu.eci.model.Assistance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssistanceRepository extends MongoRepository<Assistance, String> {
    boolean existsByUserIdAndClassId(String userId, String classId);
    long countByClassId(String claseId);
    List<Assistance> findByConfirmTrue();
    void deleteByUserIdAndClassId(String userId, String classId);
    int countByUserIdAndConfirmTrueAndStartTimeBetween(String userId, LocalDateTime startTime, LocalDateTime startTime2);
    int countByUserIdAndClassIdAndConfirmTrue(String userId, String classId);
    List<Assistance> findByConfirmFalseAndStartTimeIsBefore(LocalDateTime now);
    List<Assistance> findByConfirmFalseAndClassIdIn(List<String> classIds);
    Optional<Assistance> findByUserIdAndClassIdAndSessionId(String userId, String classId, String sessionId);
    List<Assistance> findByUserIdAndConfirmFalseAndClassIdIn(String userId, List<String> classIds);
    @Query("SELECT COUNT(DISTINCT a.userId) FROM Assistance a WHERE a.classId = classId")
    long countDistinctUsersByClassId(String classId);
    List<Assistance> findByStartTimeAfterAndConfirmIsFalse(LocalDateTime dateTime);
    List<Assistance> findByUserIdAndStartTimeAfterAndConfirmIsFalse(String userId, LocalDateTime dateTime);
    List<Assistance> findByUserIdAndConfirmIsTrue(String userId);
}
