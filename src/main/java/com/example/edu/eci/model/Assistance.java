package com.example.edu.eci.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "assistances")
public class Assistance {
    @Id
    private String id;

    private String userId;
    private String classId;
    private boolean confirm;

}