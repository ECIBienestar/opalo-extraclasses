package com.example.edu.eci.controller;

import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;
import com.example.edu.eci.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

@Tag(name = "User", description = "API para obtener la informacion de los usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/id")
    @Operation(
            summary = "Obtener usuario por id",
            description = "Devuelve usuario por id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "usuario encontrado"),
                    @ApiResponse(responseCode = "204", description = "No hay usuarios con esa id")
            }
    )
    public ResponseEntity<User> getUsersbyId(
            @Parameter(description = "ID del usuario", required = true, example = "123")
            @RequestParam String userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Crea un nuevo usuario en base con los datos proporcionados desde el modulo de usuarios",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario añadido exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud")
            }
    )
    public ResponseEntity<com.example.edu.eci.model.User> createClass(@Valid @RequestBody com.example.edu.eci.model.User newUser) {
        User createdUser = userService.createUser(newUser);
        return ResponseEntity.status(201).body(createdUser);
    }

}
