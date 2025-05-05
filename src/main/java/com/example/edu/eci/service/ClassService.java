package com.example.edu.eci.service;

import com.example.edu.eci.model.Class;
import com.example.edu.eci.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return classRepository.save(newClass);
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
}