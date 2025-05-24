package com.example.edu.eci.model;

import com.example.edu.eci.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {
    @Id
    private String id;
    private String name;
    private Role type;

    @Indexed(unique = true)
    private String identification;

    @Indexed(unique = true)
    private String email;

}
