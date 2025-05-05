package com.example.edu.eci.controller;

import com.example.edu.eci.service.AssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Asistencia", description = "API para gestionar asistencias a clases")

@RestController
@RequestMapping("/api/assistance")
public class AssistanceController {

    @Autowired
    private AssistanceService assistanceService;

    @PostMapping("/{userId}/class/{classId}/confirm")
    @Operation(
            summary = "Confirmar asistencia",
            description = "Confirma la asistencia de un usuario a una clase específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asistencia confirmada"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<String> confirmAssistance(@Parameter(description = "ID del usuario", required = true, example = "123") @PathVariable String userId,
                                                    @Parameter(description = "ID de la clase", required = true, example = "456") @PathVariable String classId) {
        try {
            assistanceService.confirmAssistance(userId, classId);
            return ResponseEntity.ok("Asistencia confirmada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
