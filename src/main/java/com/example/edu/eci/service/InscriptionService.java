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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;

@Service
public class InscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AssistanceRepository assistanceRepository;

    public void inscribeUser(String userId, String classId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Class> claseOpt = classRepository.findById(classId);

        if (userOpt.isEmpty() || claseOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario o clase no encontrada");
        }

        Class clase = claseOpt.get();

        boolean inscribed = assistanceRepository.existsByUserIdAndClassId(userId, classId);
        if (inscribed) {
            throw new IllegalStateException("Usuario ya inscrito en la clase");
        }

        long inscribedCount = assistanceRepository.countByClassId(classId);
        if (inscribedCount >= clase.getMaxStudents()) {
            throw new IllegalStateException("Capacidad máxima alcanzada");
        }

        Assistance classAssistance = new Assistance();
        classAssistance.setUserId(userId);
        classAssistance.setClassId(classId);
        classAssistance.setConfirm(false);
        assistanceRepository.save(classAssistance);

        clase.getSessions().forEach(session -> {
            Assistance sessionAssistance = new Assistance();
            sessionAssistance.setUserId(userId);
            sessionAssistance.setClassId(classId);
            sessionAssistance.setSessionId(session.getId());
            sessionAssistance.setConfirm(false);
            assistanceRepository.save(sessionAssistance);
        });
    }


    public List<Assistance> getAssistancesWithFalseAfter() {
        LocalDate today = LocalDate.now();

        List<String> activeClassIds = classRepository.findByEndDateAfterOrEndDateEquals(today,today)
                .stream()
                .map(Class::getId)
                .collect(Collectors.toList());

        return assistanceRepository.findByConfirmFalseAndClassIdIn(activeClassIds);
    }

    public List<Assistance> getPendingAssistancesByUser(String userId) {
        LocalDate today = LocalDate.now();

        List<String> activeClassIds = classRepository.findByEndDateAfterOrEndDateEquals(today, today)
                .stream()
                .map(Class::getId)
                .collect(Collectors.toList());

        return assistanceRepository.findByUserIdAndConfirmFalseAndClassIdIn(userId, activeClassIds);
    }



    public void deleteInscription(String userId, String classId) {
        if (!assistanceRepository.existsByUserIdAndClassId(userId, classId)) {
            throw new IllegalArgumentException("La inscripción no existe.");
        }
        assistanceRepository.deleteByUserIdAndClassId(userId, classId);
    }
}
