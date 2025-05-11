package com.example.edu.eci.repository;
import com.example.edu.eci.model.Class;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
    List<Class> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime startTime2);
    List<Class> findClassByType(String type);
}

