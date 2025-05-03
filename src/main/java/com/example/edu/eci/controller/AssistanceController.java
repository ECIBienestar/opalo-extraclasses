package com.example.edu.eci.controller;

import com.example.edu.eci.service.AssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistance")
public class AssistanceController {

    @Autowired
    private AssistanceService assistanceService;

    @PostMapping("/{userId}/class/{classId}/confirm")
    public ResponseEntity<String> confirmAssistance(@PathVariable String userId, @PathVariable String classId) {
        try {
            assistanceService.confirmAssistance(userId, classId);
            return ResponseEntity.ok("Asistencia confirmada exitosamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
