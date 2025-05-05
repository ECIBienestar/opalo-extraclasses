package com.example.edu.eci.config;

import com.example.edu.eci.model.*;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

private final UserRepository userRepo;
private final ClassRepository classRepo;

public DataSeeder(UserRepository userRepo, ClassRepository classRepo) {
this.userRepo = userRepo;
this.classRepo = classRepo;
}

@Override
public void run(String... args) {
if (userRepo.count() == 0) {
userRepo.save(new User("123", "Juan","Student","12345678", "juan@gmail.com"));
userRepo.save(new User("456", "Maria", "Student","12345678","maria@mail.com"));
}

if (classRepo.count() == 0) {
Class clase = new Class();
clase.setId("abc123");
clase.setName("Taller de pintura");
clase.setStartTime(LocalDate.of(2023, 10, 1));
clase.setMaxStudents(10);
clase.setResources(List.of(new Class.Equipment("Pinceles",4), new Class.Equipment("Lienzos",6)));
classRepo.save(clase);
}
}
}
