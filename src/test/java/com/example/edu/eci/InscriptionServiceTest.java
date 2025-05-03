package com.example.edu.eci;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.repository.UserRepository;
import com.example.edu.eci.service.InscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InscriptionServiceTest {

    @InjectMocks
    private InscriptionService inscriptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private AssistanceRepository assistanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInscribeUserSuccessfully() {
        String userId = "user1";
        String classId = "class1";

        User user = new User();
        Class clase = new Class();
        clase.setMaxStudents(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(classRepository.findById(classId)).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId(userId, classId)).thenReturn(false);
        when(assistanceRepository.countByClassId(classId)).thenReturn(1L);

        assertDoesNotThrow(() -> inscriptionService.inscribeUser(userId, classId));

        verify(assistanceRepository).save(any(Assistance.class));
    }

    @Test
    void testInscribeUserFailsWhenUserNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void testInscribeUserFailsWhenClassNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void testInscribeUserFailsWhenAlreadyInscribed() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void testInscribeUserFailsWhenCapacityFull() {
        Class clase = new Class();
        clase.setMaxStudents(1);

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        when(assistanceRepository.countByClassId("class1")).thenReturn(1L);

        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }
}

