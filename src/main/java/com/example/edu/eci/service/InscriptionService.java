package com.example.edu.eci.service;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        assistanceRepository.save(assistance);
    }
}
