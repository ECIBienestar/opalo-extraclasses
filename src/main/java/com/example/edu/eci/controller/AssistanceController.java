package com.example.edu.eci.controller;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.service.AssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Asistencia", description = "API para gestionar asistencias a clases")

@RestController
@RequestMapping("/api/assistance")
public class AssistanceController {

    @Autowired
    private AssistanceService assistanceService;
    @GetMapping("/attendances")
    @Operation(
            summary = "asistencias confirmadas",
            description = "Obtiene todas las asistencias confirmadas"
    )
    public ResponseEntity<List<Assistance>> getAllReserves() {
        List<Assistance> asistances = assistanceService.getAssistancesWithTrue();
        if (asistances.isEmpty() ) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(asistances);
    }

    @PostMapping("/confirm")
    @Operation(
            summary = "Confirmar asistencia",
            description = "Confirma la asistencia de un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asistencia confirmada"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
                    @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
            }
    )
    public ResponseEntity<String> confirmAssistance(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId,
            @Parameter(description = "ID del instructor que registra la asistencia", required = true, example = "123")
            @RequestParam String instructorId,
            @Parameter(description = "ID de la clase", required = true, example = "abc123")
            @RequestParam String classId) {

        try {
            assistanceService.confirmAssistance(userId, classId, instructorId);
            return ResponseEntity.ok("Asistencia confirmada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/confirmed")
    @Operation(
            summary = "Obtener asistencias en un periodo de tiempo",
            description = "Obtiene la cantidad de clases a las que ha asistido un usuario en un periodo de tiempo dado"
    )
    public ResponseEntity<?> numberClassesAttended(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId,
            @Parameter(description = "Fecha inicio periodo", required = true, example = "YYYY-MM-DD")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "Fecha fin periodo", required = true, example = "YYYY-MM-DD")
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
            summary = "Obtener asistencias dada una clase",
            description = "Obtiene la cantidad de clases a las que ha asistido un usuario dada una clase"
    )
    public ResponseEntity<?> numberClassesAttendedByClass(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId,
            @Parameter(description = "ID de la clase", required = true, example = "abc123")
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
            summary = "inasistencias",
            description = "Obtiene todas las inasistencias"
    )
    public ResponseEntity<List<Assistance>> getAllAbsences() {
        List<Assistance> asistances = assistanceService.getAssistancesWithFalseBefore();
        if (asistances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(asistances);
    }
}
