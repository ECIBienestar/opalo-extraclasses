package com.example.edu.eci.service;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.repository.AssistanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalTime.now;

@Service
public class AssistanceService {

    @Autowired
    private AssistanceRepository assistanceRepository;
    public void confirmAssistance(String usuarioId, String claseId, String instructorId) {
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
        assistance.setInstructorId(instructorId);
        assistanceRepository.save(assistance);
    }
    public List<Assistance> getAssistancesWithTrue() {
        return assistanceRepository.findByConfirmTrue();
    }

    //Obtener el número de asistencias confirmadas en un rango de fechas
    public long countConfirmedAttendances(String userId, LocalDate start, LocalDate end) {
        int assetsNumb = assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(userId, start, end);
        if (assetsNumb == 0){
            throw new IllegalStateException("No hay asistencias registradas para este usuario");
        }
        return assetsNumb;
    }

    //Obtener el número de asistencias confirmadas en una clase específica
    public long countAttendancesByClass(String userId, String classId) {
        int assetsNumb = assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId);
        if (assetsNumb == 0){
            throw new IllegalStateException("No hay asistencias registradas");
        }
        return assetsNumb;
    }

    public List<Assistance> getAssistancesWithFalseBefore() {
        return assistanceRepository.findByConfirmFalseAndStartTimeIsBefore(LocalDateTime.now().with(now()));
    }

}