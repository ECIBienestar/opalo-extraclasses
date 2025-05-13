package com.example.edu.eci.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "classes")
public class Class{
    @Id
    private String id;
    private String name;
    private int MaxStudents;
    private String type;
    private LocalDateTime StartTime;
    private LocalDateTime EndTime;
    private String repetition;
    private LocalDateTime EndTimeRepetition;
    private List<Equipment> resources;
    private String instructorId;
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Equipment {
        private String nombre;
        private int cantidad;

    }

}
