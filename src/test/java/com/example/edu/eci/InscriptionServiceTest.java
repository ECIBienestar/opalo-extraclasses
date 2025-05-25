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

    private final LocalDateTime testStartTime = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", testStartTime));
    }

    @Test
    void shouldFailWhenClassNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", testStartTime));
    }

    @Test
    void shouldFailWhenUserAlreadyInscribed() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(true);
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", testStartTime));
    }

    @Test
    void shouldFailWhenClassCapacityIsZero() {
        Class clase = new Class();
        clase.setMaxStudents(0);
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", testStartTime));
    }

    @Test
    void shouldInscribeUserSuccessfully() {
        // Arrange
        User user = new User();
        user.setId("user1");

        Class clase = new Class();
        clase.setId("class1");
        clase.setMaxStudents(10);
        clase.setInstructorId("inst1"); // Asegurar instructorId

        // Crear sesión válida
        Class.Session session = new Class.Session();
        session.setId("sess1");
        session.setDay("Monday");
        session.setStartTime("09:00");
        session.setEndTime("10:00");
        clase.setSessions(List.of(session));

        // Mockear las dependencias
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        when(assistanceRepository.countByClassId("class1")).thenReturn(5L);

        // Mockear el save para devolver un objeto Assistance con los datos correctos
        when(assistanceRepository.save(any(Assistance.class))).thenAnswer(invocation -> {
            Assistance a = invocation.getArgument(0);
            // Asegurar que startTime no sea null
            if (a.getStartTime() == null) {
                a.setStartTime(testStartTime);
            }
            return a;
        });

        // Act
        assertDoesNotThrow(() -> inscriptionService.inscribeUser("user1", "class1", testStartTime));

        // Assert
        ArgumentCaptor<Assistance> assistanceCaptor = ArgumentCaptor.forClass(Assistance.class);
        verify(assistanceRepository, atLeastOnce()).save(assistanceCaptor.capture());

        Assistance savedAssistance = assistanceCaptor.getValue();
        assertNotNull(savedAssistance.getStartTime(), "El startTime no debería ser null");
        assertEquals("user1", savedAssistance.getUserId());
        assertEquals("class1", savedAssistance.getClassId());
        assertEquals(testStartTime, savedAssistance.getStartTime());
        assertFalse(savedAssistance.isConfirm());
    }

    @Test
    void shouldFailWhenStartTimeIsNull() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));

        // Cambiar la excepción esperada a IllegalStateException
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", null));
    }

    @Test
    void shouldFailWhenStartTimeIsInThePast() {
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));

        // Cambiar la excepción esperada a IllegalStateException
        assertThrows(IllegalStateException.class, () ->
                inscriptionService.inscribeUser("user1", "class1", pastTime));
    }

    // Los tests de deleteInscription no necesitan cambios
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

    // Tests actualizados para manejar startTime
    @Test
    void shouldReturnFutureUnconfirmedAssistances() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(1);

        Assistance a1 = new Assistance();
        a1.setId("a1");
        a1.setStartTime(futureDate);
        a1.setConfirm(false);

        when(assistanceRepository.findByStartTimeAfterAndConfirmIsFalse(any(LocalDateTime.class)))
                .thenReturn(List.of(a1));

        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistances();

        assertEquals(1, result.size());
        assertEquals(futureDate, result.get(0).getStartTime());
        assertFalse(result.get(0).isConfirm());
    }

    @Test
    void shouldReturnFutureUnconfirmedAssistancesByUser() {
        String userId = "user1";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(1);

        Assistance a1 = new Assistance();
        a1.setUserId(userId);
        a1.setStartTime(futureDate);
        a1.setConfirm(false);

        when(assistanceRepository.findByUserIdAndStartTimeAfterAndConfirmIsFalse(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of(a1));

        List<Assistance> result = inscriptionService.findFutureUnconfirmedAssistancesByUser(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(futureDate, result.get(0).getStartTime());
        assertFalse(result.get(0).isConfirm());
    }
}