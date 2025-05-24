package com.example.edu.eci.config;

import com.example.edu.eci.model.*;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.enums.Role;
import com.example.edu.eci.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

private final UserRepository userRepo;

public DataSeeder(UserRepository userRepo, ClassRepository classRepo) {
this.userRepo = userRepo;
}

@Override
public void run(String... args) {
if (userRepo.count() == 0) {
userRepo.save(new User("4795", "Emily", Role.valueOf("Student"),"9864573", "emily@gmail.com"));
}
}
}
