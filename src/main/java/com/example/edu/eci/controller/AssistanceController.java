package com.example.edu.eci.controller;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.service.AssistanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Asistencia", description = "API para gestionar asistencias a clases")

@RestController
@RequestMapping("/api/assistance")
public class AssistanceController {

    @Autowired
    private AssistanceService assistanceService;

    @GetMapping("/confirmed")
    @Operation(
            summary = "Asistencias confirmadas",
            description = "Obtiene todas las asistencias confirmadas"
    )
    public ResponseEntity<List<Assistance>> getAllConfirmed() {
        List<Assistance> assistances = assistanceService.getAssistancesWithTrue();
        return assistances.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(assistances);
    }

    @PostMapping("/confirm")
    @Operation(
            summary = "Confirmar asistencia a una sesión",
            description = "Confirma la asistencia de un usuario a una sesión específica de una clase",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asistencia confirmada"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
            }
    )
    public ResponseEntity<String> confirmAssistance(
            @Parameter(description = "ID del usuario", required = true) @RequestParam String userId,
            @Parameter(description = "ID del instructor", required = true) @RequestParam String instructorId,
            @Parameter(description = "ID de la clase", required = true) @RequestParam String classId,
            @Parameter(description = "ID de la sesión", required = true) @RequestParam String SessionId
    ) {
        try {
            assistanceService.confirmAssistance(userId, classId, SessionId, instructorId);
            return ResponseEntity.ok("Asistencia confirmada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/confirmed")
    @Operation(
            summary = "Cantidad de asistencias confirmadas en un periodo",
            description = "Obtiene el número de asistencias confirmadas por un usuario en un periodo de tiempo"
    )
    public ResponseEntity<?> numberClassesAttended(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        try {
            long count = assistanceService.countConfirmedAttendances(userId, start, end);
            return ResponseEntity.ok(count);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/class")
    @Operation(
            summary = "Cantidad de asistencias confirmadas por clase",
            description = "Obtiene el número de asistencias confirmadas de un usuario en una clase específica"
    )
    public ResponseEntity<?> numberClassesAttendedByClass(
            @RequestParam String userId,
            @RequestParam String classId
    ) {
        try {
            long count = assistanceService.countAttendancesByClass(userId, classId);
            return ResponseEntity.ok(count);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/absences")
    @Operation(
            summary = "Obtener inasistencias",
            description = "Obtiene todas las asistencias no confirmadas en sesiones pasadas"
    )
    public ResponseEntity<List<Assistance>> getAllAbsences() {
        List<Assistance> assistances = assistanceService.getAssistancesWithFalseBefore();
        return assistances.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(assistances);
    }
    @GetMapping("/my-Historical")
    @Operation(
            summary = "Obtener historico de asistencias por usuario",
            description = "Devuelve todas las asistencias confirmadas de un usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "historial encontradas"),
                    @ApiResponse(responseCode = "204", description = "No hay Historial")
            }
    )
    public ResponseEntity<List<Assistance>> getPendingAssistancesByUser(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId) {

        List<Assistance> assistances = assistanceService.findAssistancesHistoricByUser(userId);

        return assistances.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(assistances);
    }
}

