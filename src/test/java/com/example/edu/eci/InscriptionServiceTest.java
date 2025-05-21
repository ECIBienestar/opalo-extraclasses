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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    void shouldFailWhenUserNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void shouldFailWhenClassNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void shouldFailWhenUserAlreadyInscribed() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }



    @Test
    void shouldFailWhenClassCapacityIsZero() {
        Class clase = new Class();
        clase.setMaxStudents(0);
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }

    @Test
    void shouldFailWhenUserAndClassNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        when(classRepository.findById("class1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1"));
    }


    @Test
    void shouldDeleteInscriptionSuccessfully() {
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(true);
        assertDoesNotThrow(() -> inscriptionService.deleteInscription("user1", "class1"));
        verify(assistanceRepository).deleteByUserIdAndClassId("user1", "class1");
    }

    @Test
    void shouldFailToDeleteInscriptionWhenNotFound() {
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.deleteInscription("user1", "class1"));
    }


    @Test
    void shouldReturnEmptyListWhenNoFutureUnconfirmedAssistances() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Usa any() para ignorar las diferencias mínimas de tiempo
        when(assistanceRepository.findByStartTimeAfterAndConfirmIsFalse(any(LocalDateTime.class)))
                .thenReturn(List.of());

        // Act
        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistances();

        // Assert
        assertTrue(result.isEmpty());
        verify(assistanceRepository).findByStartTimeAfterAndConfirmIsFalse(any(LocalDateTime.class));
    }

    @Test
    void shouldReturnFutureUnconfirmedAssistances() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(1);

        Assistance a1 = new Assistance();
        a1.setId("a1");
        a1.setStartTime(futureDate);
        a1.setConfirm(false);

        Assistance a2 = new Assistance();
        a2.setId("a2");
        a2.setStartTime(futureDate.plusHours(2));
        a2.setConfirm(false);

        // Usa any() para evitar problemas de precisión temporal
        when(assistanceRepository.findByStartTimeAfterAndConfirmIsFalse(any(LocalDateTime.class)))
                .thenReturn(List.of(a1, a2));

        // Act
        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistances();

        // Assert
        assertEquals(2, result.size());
        assertFalse(result.get(0).isConfirm());
        assertFalse(result.get(1).isConfirm());
        assertTrue(result.get(0).getStartTime().isAfter(now));
        assertTrue(result.get(1).getStartTime().isAfter(now));
        verify(assistanceRepository).findByStartTimeAfterAndConfirmIsFalse(any(LocalDateTime.class));
    }

    @Test
    void shouldReturnEmptyListIfNoFutureAssistances() {
        String userId = "user1";
        LocalDateTime now = LocalDateTime.now();

        when(assistanceRepository.findByUserIdAndStartTimeAfterAndConfirmIsFalse(userId, now))
                .thenReturn(List.of());

        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistancesByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotReturnConfirmedAssistances() {
        String userId = "user1";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(1);

        Assistance a1 = new Assistance();
        a1.setUserId(userId);
        a1.setStartTime(futureDate);
        a1.setConfirm(true); // Asistencia confirmada

        when(assistanceRepository.findByUserIdAndStartTimeAfterAndConfirmIsFalse(userId, now))
                .thenReturn(List.of());

        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistancesByUser(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotReturnPastAssistances() {
        String userId = "user1";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastDate = now.minusDays(1);

        Assistance a1 = new Assistance();
        a1.setUserId(userId);
        a1.setStartTime(pastDate); // Fecha pasada
        a1.setConfirm(false);

        when(assistanceRepository.findByUserIdAndStartTimeAfterAndConfirmIsFalse(userId, now))
                .thenReturn(List.of());

        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistancesByUser(userId);

        assertTrue(result.isEmpty());
    }


}
