package com.example.edu.eci.controller;

import com.example.edu.eci.service.InscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions")


public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;

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
}

