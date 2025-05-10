package com.example.edu.eci.service;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.repository.AssistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssistanceService {

    @Autowired
    private AssistanceRepository assistanceRepository;
    public void confirmAssistance(String usuarioId, String claseId) {
        Optional<Assistance> asistenciaOpt = assistanceRepository
                .findByUserIdAndClassId(usuarioId, claseId);

        if (asistenciaOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe inscripción para confirmar asistencia");
        }

        Assistance assistance = asistenciaOpt.get();

        if (assistance.isConfirm()) {
            throw new IllegalStateException("Asistencia ya confirmada");
        }

        assistance.setConfirm(true);
        assistanceRepository.save(assistance);
    }
    public List<Assistance> getAssistancesWithTrue() {
        return assistanceRepository.findByConfirmTrue();
    }
}