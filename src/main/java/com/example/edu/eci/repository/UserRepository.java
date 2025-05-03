package com.example.edu.eci.repository;

import com.example.edu.eci.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Puedes agregar búsquedas personalizadas si lo necesitas, por ejemplo:
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentification(String identification);
}
