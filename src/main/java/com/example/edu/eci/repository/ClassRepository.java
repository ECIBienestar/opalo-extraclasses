package com.example.edu.eci.repository;
import com.example.edu.eci.model.Class;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
    List<Class> findByStartTimeBetween(LocalDate start, LocalDate end);
}

