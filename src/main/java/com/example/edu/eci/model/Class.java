package com.example.edu.eci.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "classes")
public class Class{
    @Id
    private String id;
    private String name;
    private int MaxStudents;
    private LocalDate StartTime;
    private LocalDate EndTime;
    private List<Equipment> resources;
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Equipment {
        private String nombre;
        private int cantidad;

    }

}
