package com.example.edu.eci.repository;
import com.example.edu.eci.model.Class;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
    @Query("{ 'sessions': { $elemMatch: { 'day': ?0, 'startTime': { $gte: ?1 }, 'endTime': { $lte: ?2 } } } }")
    List<Class> findByStartTimeBetween(String day, String startTime, String endTime);
    List<Class> findClassByType(String type);
    List<Class> findByEndDateAfterOrEndDateEquals(LocalDate endDate, LocalDate endDate2);
}

