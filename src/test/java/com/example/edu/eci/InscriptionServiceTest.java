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
        when(classRepository.findByEndDateAfterOrEndDateEquals(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(assistanceRepository.findByConfirmFalseAndClassIdIn(List.of()))
                .thenReturn(List.of());

        List<Assistance> result = inscriptionService.getAssistancesWithFalseAfter();
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnFutureUnconfirmedAssistances() {
        Assistance a1 = new Assistance();
        a1.setUserId("u1"); a1.setClassId("c1"); a1.setConfirm(false);
        Assistance a2 = new Assistance();
        a2.setUserId("u2"); a2.setClassId("c2"); a2.setConfirm(false);

        Class activeClass1 = new Class(); activeClass1.setId("c1");
        Class activeClass2 = new Class(); activeClass2.setId("c2");

        when(classRepository.findByEndDateAfterOrEndDateEquals(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(activeClass1, activeClass2));

        when(assistanceRepository.findByConfirmFalseAndClassIdIn(List.of("c1", "c2")))
                .thenReturn(List.of(a1, a2));

        List<Assistance> result = inscriptionService.getAssistancesWithFalseAfter();

        assertEquals(2, result.size());
        assertFalse(result.get(0).isConfirm());
        assertFalse(result.get(1).isConfirm());
    }

    @Test
    void shouldReturnPendingAssistancesByUser() {
        String userId = "user1";
        LocalDate today = LocalDate.now();

        Class activeClass = new Class();
        activeClass.setId("class1");
        activeClass.setEndDate(today.plusDays(1));

        Assistance a1 = new Assistance();
        a1.setUserId(userId);
        a1.setClassId("class1");
        a1.setConfirm(false);

        when(classRepository.findByEndDateAfterOrEndDateEquals(today, today))
                .thenReturn(List.of(activeClass));

        when(assistanceRepository.findByUserIdAndConfirmFalseAndClassIdIn(userId, List.of("class1")))
                .thenReturn(List.of(a1));

        List<Assistance> result = inscriptionService.getPendingAssistancesByUser(userId);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
        assertEquals("class1", result.get(0).getClassId());
        assertFalse(result.get(0).isConfirm());
    }


    @Test
    void shouldReturnEmptyListIfNoActiveClasses() {
        String userId = "user1";
        LocalDate today = LocalDate.now();

        when(classRepository.findByEndDateAfterOrEndDateEquals(today, today))
                .thenReturn(List.of());

        when(assistanceRepository.findByUserIdAndConfirmFalseAndClassIdIn(userId, List.of()))
                .thenReturn(List.of());

        List<Assistance> result = inscriptionService.getPendingAssistancesByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
