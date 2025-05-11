package com.example.edu.eci.controller;

import com.example.edu.eci.model.Class;
import com.example.edu.eci.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Clase", description = "API para gestionar clases")
@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping
    @Operation(
            summary = "Obtener todas las clases",
            description = "Obtiene una lista de todas las clases registradas"
    )
    public ResponseEntity<List<Class>> getAllClasses() {
        List<Class> classes = classService.getAllClasses();
        if (classes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/class")
    @Operation(
            summary = "Obtener clase por ID",
            description = "Obtiene los detalles de una clase específica por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clase encontrada"),
                    @ApiResponse(responseCode = "404", description = "Clase no encontrada")
            }
    )
    public ResponseEntity<Class> getClassById(
            @Parameter(description = "ID de la clase", required = true, example = "abc123")
            @RequestParam String classId) {
        return classService.getClassById(classId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva clase",
            description = "Crea una nueva clase con los datos proporcionados",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Clase creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<List<Class>> createClass(@Valid @RequestBody Class newClass) {
        List<Class> createdClasses = classService.createClass(newClass);
        return ResponseEntity.status(201).body(createdClasses);
    }


    @PutMapping("/update")
    @Operation(
            summary = "Actualizar una clase",
            description = "Actualiza los detalles de una clase existente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clase actualizada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Clase no encontrada")
            }
    )
    public ResponseEntity<Class> updateClass(
            @Parameter(description = "ID de la clase a actualizar", required = true, example = "abc123")
            @RequestParam String id,
            @Parameter(description = "Datos actualizados de la clase", required = true)
            @Valid @RequestBody Class updatedClass) {
        return classService.updateClass(id, updatedClass)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "Eliminar una clase",
            description = "Elimina una clase específica por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clase eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Clase no encontrada")
            }
    )
    public ResponseEntity<Void> deleteClass(
            @Parameter(description = "ID de la clase a eliminar", required = true, example = "abc123")
            @RequestParam String id) {
        if (classService.deleteClass(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/type")
    @Operation(
            summary = "Obtener clases por tipo",
            description = "Obtiene una lista de clases según el tipo de actividad",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clases encontradas"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron clases del tipo especificado")
            }
    )
    public ResponseEntity<List<Class>> getClassesByType(
            @Parameter(description = "Tipo de la clase/actividad", required = true, example = "Deportiva, Cultural, Artística, etc.")
            @RequestParam String classType) {

        List<Class> classes = classService.getClassesByType(classType);
        if (classes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(classes);
    }

}