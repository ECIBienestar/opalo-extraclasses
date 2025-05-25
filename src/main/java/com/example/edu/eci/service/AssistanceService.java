package com.example.edu.eci.service;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssistanceService {

    @Autowired
    private AssistanceRepository assistanceRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    public void confirmAssistance(String userId, String classId, String sessionId, String instructorId) {
        Optional<Assistance> assistanceOpt = assistanceRepository.findByUserIdAndClassIdAndSessionId(userId, classId, sessionId);

        if (assistanceOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe inscripción para esta sesión");
        }

        Assistance assistance = assistanceOpt.get();

        if (assistance.isConfirm()) {
            throw new IllegalStateException("Asistencia ya confirmada para esta sesión");
        }

        assistance.setConfirm(true);
        assistance.setInstructorId(instructorId);
        assistanceRepository.save(assistance);
    }

    public List<Assistance> getAssistancesWithTrue() {
        return assistanceRepository.findByConfirmTrue();
    }

    public long countConfirmedAttendances(String userId, LocalDate start, LocalDate end) {
        long confirmedCount = assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
        if (confirmedCount == 0) {
            throw new IllegalStateException("No hay asistencias registradas para este usuario");
        }
        return confirmedCount;
    }

    public long countAttendancesByClass(String userId, String classId) {
        long confirmedCount = assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId);
        if (confirmedCount == 0) {
            throw new IllegalStateException("No hay asistencias registradas");
        }
        return confirmedCount;
    }

    public List<Assistance> getAssistancesWithFalseBefore() {
        return assistanceRepository.findByConfirmFalseAndStartTimeIsBefore(LocalDateTime.now());
    }

    public void completeInscription(String userId, String classId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Class> classOpt = classRepository.findById(classId);

        if (userOpt.isEmpty() || classOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario o clase no encontrada");
        }

        Class clase = classOpt.get();

        boolean alreadyInscribed = assistanceRepository.existsByUserIdAndClassId(userId, classId);
        if (alreadyInscribed) {
            throw new IllegalStateException("Usuario ya inscrito en esta clase");
        }

        long inscribedCount = assistanceRepository.countByClassId(classId);
        if (inscribedCount >= clase.getMaxStudents()) {
            throw new IllegalStateException("Capacidad máxima alcanzada");
        }

        for (Class.Session session : clase.getSessions()) {
            Assistance assistance = new Assistance();
            assistance.setUserId(userId);
            assistance.setClassId(classId);
            assistance.setSessionId(session.getId());
            assistance.setStartTime(LocalDate.now());
            assistance.setConfirm(false);
            assistanceRepository.save(assistance);
        }
    }
    public List<Assistance> findAssistancesHistoricByUser(String userId) {
        return assistanceRepository.findByUserIdAndConfirmIsTrue(userId);
    }
}
