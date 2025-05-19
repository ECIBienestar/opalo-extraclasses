package com.example.edu.eci.model;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "assistances")
public class Assistance {
    @Id
    private String id;
    private LocalDateTime startTime;
    private String userId;
    private String instructorId;
    private String classId;
    private String SessionId;
    private boolean confirm;

}