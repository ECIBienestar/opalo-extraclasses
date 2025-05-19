package com.example.edu.eci.service;

import com.example.edu.eci.model.Class;
import com.example.edu.eci.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public Optional<Class> getClassById(String id) {
        return classRepository.findById(id);
    }

    public Class createClass(Class newClass) {
        Class savedClass = classRepository.save(newClass);
        if (newClass.getSessions() != null && !newClass.getSessions().isEmpty()) {
            List<Class.Session> repeatedSessions = generateRepeatedSessions(newClass);
            savedClass.getSessions().addAll(repeatedSessions);
            classRepository.save(savedClass);
        }

        return savedClass;
    }

    public Optional<Class> updateClass(String id, Class updatedClass) {
        if (classRepository.existsById(id)) {
            updatedClass.setId(id);
            return Optional.of(classRepository.save(updatedClass));
        }
        return Optional.empty();
    }

    public List<Class> getClassesByType(String type) {
        return classRepository.findClassByType(type);
    }

    public boolean deleteClass(String id) {
        if (classRepository.existsById(id)) {
            classRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Class> getClassesByDateRange(String day, String startTime, String endTime) {
        return classRepository.findByStartTimeBetween(day, startTime, endTime);
    }

    public List<Class.Session> generateRepeatedSessions(Class baseClass) {
        List<Class.Session> repeatedSessions = new ArrayList<>();

        if (baseClass == null || baseClass.getSessions() == null || baseClass.getSessions().isEmpty()) {
            return repeatedSessions;
        }
        LocalDate repetitionEndDate = baseClass.getEndDate();
        if (repetitionEndDate == null) {
            return repeatedSessions;
        }

        Class.Session initialSessionTemplate = baseClass.getSessions().get(0);

        LocalDateTime currentScheduledStartTime = LocalDateTime.parse(initialSessionTemplate.getStartTime());
        LocalDateTime currentScheduledEndTime = LocalDateTime.parse(initialSessionTemplate.getEndTime());

        ChronoUnit repetitionUnit = ChronoUnit.WEEKS;

        while (true) {
            LocalDateTime nextStartTime = currentScheduledStartTime.plus(1, repetitionUnit);
            LocalDateTime nextEndTime = currentScheduledEndTime.plus(1, repetitionUnit);

            if (nextStartTime.toLocalDate().isAfter(repetitionEndDate)) {
                break;
            }

            Class.Session repeatedSession = new Class.Session(
                    initialSessionTemplate.getId(),
                    initialSessionTemplate.getDay(),
                    nextStartTime.toString(),
                    nextEndTime.toString()
            );
            repeatedSessions.add(repeatedSession);

            currentScheduledStartTime = nextStartTime;
            currentScheduledEndTime = nextEndTime;
        }

        return repeatedSessions;
    }

}