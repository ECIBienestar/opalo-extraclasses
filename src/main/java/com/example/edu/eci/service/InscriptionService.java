package com.example.edu.eci.service;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        // Verificar si ya está inscrito
        boolean Inscribed = assistanceRepository.existsByUserIdAndClassId(userId, classId);
        if (Inscribed) {
            throw new IllegalStateException("Usuario ya inscrito en la clase");
        }

        // Verificar capacidad
        long inscribed = assistanceRepository.countByClassId(classId);
        if (inscribed >= clase.getMaxStudents()) {
            throw new IllegalStateException("Capacidad máxima alcanzada");
        }

        // Crear registro de assistance pendiente
        Assistance assistance = new Assistance();
        assistance.setUserId(userId);
        assistance.setClassId(classId);
        assistance.setConfirm(false);
        assistance.setStartTime(clase.getStartTime());

        //clase.setMaxStudents(clase.getMaxStudents() - 1);

        assistanceRepository.save(assistance);
    }

    public List<Assistance> getAssistancesWithFalseAfter() {
        return assistanceRepository.findByConfirmFalseAndStartTimeIsAfter(LocalDateTime.now().with(now()));
    }

    public void deleteInscription(String userId, String classId) {
        if (!assistanceRepository.existsByUserIdAndClassId(userId, classId)) {
            throw new IllegalArgumentException("La inscripción no existe.");
        }
        assistanceRepository.deleteByUserIdAndClassId(userId, classId);
    }
}
