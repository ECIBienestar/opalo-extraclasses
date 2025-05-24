package com.example.edu.eci.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "classes")
public class Class {
    @Id
    private String id;
    private String name;
    private int maxStudents;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Session> sessions;
    private List<Equipment> resources;
    private String instructorId;
    private String repetition; // Tipo de repetición (e.g., "weekly", "monthly")
    private LocalDate endTimeRepetition;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Equipment {
        private String name;
        private int quantity;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Session {
        private String id;
        private String day;
        private String startTime;
        private String endTime;
    }
}
