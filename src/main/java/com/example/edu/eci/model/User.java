package com.example.edu.eci.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {
    @Id
    private String id;
    private String name;
    private String type; // Ejemplo: "estudiante", "docente"
    private String identification;
    private String email;

}
