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

    public List<Class> createClass(Class newClass) {
        List<Class> createdClasses = new ArrayList<>();

        Class savedClass = classRepository.save(newClass);
        createdClasses.add(savedClass);

        if (newClass.getRepetition() != null && newClass.getEndTimeRepetition() != null) {
            List<Class> repeatedClasses = generateRepeatedClasses(savedClass);
            classRepository.saveAll(repeatedClasses);
            createdClasses.addAll(repeatedClasses);
        }
        return createdClasses;
    }

    public Optional<Class> updateClass(String id, Class updatedClass) {
        if (classRepository.existsById(id)) {
            updatedClass.setId(id);
            return Optional.of(classRepository.save(updatedClass));
        }
        return Optional.empty();
    }

    public boolean deleteClass(String id) {
        if (classRepository.existsById(id)) {
            classRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Class> getClassesByDateRange(LocalDateTime start, LocalDateTime end) {
        return classRepository.findByStartTimeBetween(start, end);
    }

    public List<Class> getClassesByType(String type) {
        return classRepository.findClassByType(type);
    }

    public List<Class> generateRepeatedClasses(Class baseClass) {
        List<Class> repeatedClasses = new ArrayList<>();

        LocalDateTime currentStart = baseClass.getStartTime();
        LocalDateTime currentEnd = baseClass.getEndTime();
        LocalDateTime repetitionEnd = baseClass.getEndTimeRepetition();

        ChronoUnit unit = switch (baseClass.getRepetition().toLowerCase()) {
            case "weekly" -> ChronoUnit.WEEKS;
            case "monthly" -> ChronoUnit.MONTHS;
            default -> throw new IllegalArgumentException("Tipo de repetición no soportado: " + baseClass.getRepetition());
        };

        while (true) {
            currentStart = currentStart.plus(1, unit);
            currentEnd = currentEnd.plus(1, unit);
            if (currentStart.isAfter(repetitionEnd)) break;

            Class repeated = new Class();
            repeated.setType(baseClass.getType());
            repeated.setStartTime(currentStart);
            repeated.setEndTime(currentEnd);
            repeated.setInstructorId(baseClass.getInstructorId());
            repeated.setMaxStudents(baseClass.getMaxStudents());
            repeated.setRepetition(null);
            repeated.setEndTimeRepetition(null);

            repeatedClasses.add(repeated);
        }

        return repeatedClasses;
    }

}