package com.example.edu.eci.controller;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.service.InscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")

@Tag(name = "Inscripcion", description = "API para gestionar inscripcion a clases")

public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping
    @Operation(
            summary = "Get pending class assistances",
            description = "Retrieves all assistances that are not confirmed and belong to classes with upcoming sessions"
    )
    public ResponseEntity<List<Assistance>> getPendingAssistances() {
        List<Assistance> pendingAssistances = inscriptionService.findFutureUnconfirmedAssistances();

        if (pendingAssistances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(pendingAssistances);
    }

    @GetMapping("/my-inscriptions")
    @Operation(
            summary = "Obtener inscripciones pendientes por usuario",
            description = "Devuelve todas las asistencias no confirmadas de un usuario con fecha futura",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inscripciones encontradas"),
                    @ApiResponse(responseCode = "204", description = "No hay inscripciones pendientes")
            }
    )
    public ResponseEntity<List<Assistance>> getPendingAssistancesByUser(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId) {

        List<Assistance> assistances = inscriptionService.findFutureUnconfirmedAssistancesByUser(userId);

        return assistances.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(assistances);
    }



    @PostMapping("/inscribe")
    @Operation(
            summary = "inscribir usuario a clase",
            description = "Inscribir un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario inscrito exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<String> inscribeUser(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId,

            @Parameter(description = "ID de la clase", required = true, example = "abc123")
            @RequestParam String classId,

            @Parameter(description = "Fecha de inicio de la clase", required = true, example = "")
            @RequestParam LocalDateTime startDate)


    {

        try {
            inscriptionService.inscribeUser(userId, classId, startDate);
            return ResponseEntity.ok("Usuario inscrito exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "eliminar inscripcion de usuario a clase",
            description = "Elimina la inscripción de un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inscripción eliminada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
                    @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
            }
    )
    public ResponseEntity<String> deleteInscription(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId,

            @Parameter(description = "ID de la clase", required = true, example = "abc123")
            @RequestParam String classId) {

        try {
            inscriptionService.deleteInscription(userId, classId);
            return ResponseEntity.ok("Inscripción eliminada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

