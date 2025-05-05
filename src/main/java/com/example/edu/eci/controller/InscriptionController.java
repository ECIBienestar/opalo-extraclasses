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

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")

@Tag(name = "Inscripcion", description = "API para gestionar inscripcion a clases")

public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping
    @Operation(
            summary = "inscripciones realizadas",
            description = "Obtiene todas las inscripciones realizadas"
    )
    public ResponseEntity<List<Assistance>> getAllReserves() {
        List<Assistance> asistances = inscriptionService.getAssistancesWithFalse();
        if (asistances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(asistances);
    }

    @PostMapping("/{userId}/class/{classId}")
    @Operation(
            summary = "inscribir usuario a clase",
            description = "Inscribir un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario inscrito exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<String> inscribeUser(@Parameter(description = "ID del usuario", required = true, example = "123") @PathVariable String userId,
                                               @Parameter(description = "ID de la clase", required = true, example = "456") @PathVariable String classId) {
        try {
            inscriptionService.inscribeUser(userId, classId);
            return ResponseEntity.ok("Usuario inscrito exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/class/{classId}/delete")
    @Operation(
            summary = "eliminar inscripcion de usuario a clase",
            description = "Elimina la inscripción de un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inscripción eliminada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<String> deleteInscription(@Parameter(description = "ID del usuario", required = true, example = "123") @PathVariable String userId,
                                                    @Parameter(description = "ID de la clase", required = true, example = "456") @PathVariable String classId) {
        try {
            inscriptionService.deleteInscription(userId, classId);
            return ResponseEntity.ok("Inscripción eliminada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
}

