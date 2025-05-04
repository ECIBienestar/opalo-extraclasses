package com.example.edu.eci.controller;

import com.example.edu.eci.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;

    @PostMapping("/{userId}/class/{classId}")
    public ResponseEntity<String> inscribeUser(@PathVariable String userId, @PathVariable String classId) {
        try {
            inscriptionService.inscribeUser(userId, classId);
            return ResponseEntity.ok("Usuario inscrito exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

