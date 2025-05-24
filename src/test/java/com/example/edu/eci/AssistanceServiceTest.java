package com.example.edu.eci;
import com.example.edu.eci.model.Class;
import com.example.edu.eci.model.User;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.repository.UserRepository;
import com.example.edu.eci.service.AssistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssistanceServiceTest {

    @InjectMocks
    private AssistanceService assistanceService;

    @InjectMocks
    com.example.edu.eci.model.Class classModel;

    @Mock
    private AssistanceRepository assistanceRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void confirmAssistanceShouldThrowExceptionWhenNoInscriptionExistsForSession() {
        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1"));
    }

    @Test
    void confirmAssistanceShouldThrowExceptionWhenAssistanceAlreadyConfirmed() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setSessionId("session1");
        assistance.setConfirm(true);

        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.of(assistance));

        assertThrows(IllegalStateException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1"));
    }

    @Test
    void confirmAssistanceShouldConfirmSuccessfullyWhenValid() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setSessionId("session1");
        assistance.setConfirm(false);

        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.of(assistance));

        assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1");

        assertTrue(assistance.isConfirm());
        assertEquals("instructor1", assistance.getInstructorId());
        verify(assistanceRepository).save(assistance);
    }

    @Test
    void getAssistancesWithTrueShouldReturnListOfConfirmedAssistances() {
        Assistance assistance1 = new Assistance();
        assistance1.setConfirm(true);

        Assistance assistance2 = new Assistance();
        assistance2.setConfirm(true);

        when(assistanceRepository.findByConfirmTrue()).thenReturn(List.of(assistance1, assistance2));

        List<Assistance> result = assistanceService.getAssistancesWithTrue();

        assertEquals(2, result.size());
        assertTrue(result.get(0).isConfirm());
        assertTrue(result.get(1).isConfirm());
        verify(assistanceRepository).findByConfirmTrue();
    }

    @Test
    void getAssistancesWithFalseBeforeShouldReturnListOfUnconfirmedAssistancesBeforeNow() {
        Assistance assistance1 = new Assistance();
        assistance1.setConfirm(false);
        assistance1.setStartTime(LocalDateTime.now().minusDays(1));

        Assistance assistance2 = new Assistance();
        assistance2.setConfirm(false);
        assistance2.setStartTime(LocalDateTime.now().minusHours(2));

        when(assistanceRepository.findByConfirmFalseAndStartTimeIsBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(assistance1, assistance2));

        List<Assistance> result = assistanceService.getAssistancesWithFalseBefore();

        assertEquals(2, result.size());
        assertFalse(result.get(0).isConfirm());
        assertFalse(result.get(1).isConfirm());
        verify(assistanceRepository).findByConfirmFalseAndStartTimeIsBefore(any(LocalDateTime.class));
    }
    @Test
    void confirmAssistanceShouldThrowExceptionWhenSessionNotFound() {
        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1"));
    }

    @Test
    void confirmAssistanceShouldThrowExceptionWhenAlreadyConfirmed() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setSessionId("session1");
        assistance.setConfirm(true);

        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.of(assistance));

        assertThrows(IllegalStateException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1"));
    }

    @Test
    void confirmAssistanceShouldConfirmSuccessfully() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setSessionId("session1");
        assistance.setConfirm(false);

        when(assistanceRepository.findByUserIdAndClassIdAndSessionId("user1", "class1", "session1"))
                .thenReturn(Optional.of(assistance));

        assistanceService.confirmAssistance("user1", "class1", "session1", "instructor1");

        assertTrue(assistance.isConfirm());
        assertEquals("instructor1", assistance.getInstructorId());
        verify(assistanceRepository).save(assistance);
    }

    @Test
    void countConfirmedAttendancesShouldReturnCountWhenAttendancesExist() {
        String userId = "user1";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX)))
                .thenReturn(5);

        long result = assistanceService.countConfirmedAttendances(userId, start, end);

        assertEquals(5L, result);
        verify(assistanceRepository).countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    @Test
    void countConfirmedAttendancesShouldThrowExceptionWhenNoAttendancesExist() {
        String userId = "user1";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX)))
                .thenReturn(0);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                assistanceService.countConfirmedAttendances(userId, start, end));

        assertEquals("No hay asistencias registradas para este usuario", exception.getMessage());
        verify(assistanceRepository).countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    @Test
    void countAttendancesByClassShouldReturnCountWhenAttendancesExist() {
        String userId = "user1";
        String classId = "class1";

        when(assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId))
                .thenReturn(3);

        long result = assistanceService.countAttendancesByClass(userId, classId);

        assertEquals(3L, result);
        verify(assistanceRepository).countByUserIdAndClassIdAndConfirmTrue(userId, classId);
    }

    @Test
    void countAttendancesByClassShouldThrowExceptionWhenNoAttendancesExist() {
        String userId = "user1";
        String classId = "class1";

        when(assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId))
                .thenReturn(0);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                assistanceService.countAttendancesByClass(userId, classId));

        assertEquals("No hay asistencias registradas", exception.getMessage());
        verify(assistanceRepository).countByUserIdAndClassIdAndConfirmTrue(userId, classId);
    }
    @Test
    void completeInscriptionShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.completeInscription("user1", "class1"));
    }

    @Test
    void completeInscriptionShouldThrowExceptionWhenClassNotFound() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.completeInscription("user1", "class1"));
    }

    @Test
    void completeInscriptionShouldThrowExceptionWhenUserAlreadyInscribed() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(new Class()));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                assistanceService.completeInscription("user1", "class1"));
    }

    @Test
    void completeInscriptionShouldThrowExceptionWhenClassCapacityReached() {
        com.example.edu.eci.model.Class clase = new com.example.edu.eci.model.Class();
        clase.setMaxStudents(1);

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        when(assistanceRepository.countByClassId("class1")).thenReturn(1L);

        assertThrows(IllegalStateException.class, () ->
                assistanceService.completeInscription("user1", "class1"));
    }

    @Test
    void completeInscriptionShouldSaveAssistancesWhenValid() {
        Class.Session session1 = new Class.Session();
        session1.setId("session1");

        Class.Session session2 = new Class.Session();
        session2.setId("session2");

        Class clase = new Class();
        clase.setMaxStudents(10);
        clase.setSessions(List.of(session1, session2));

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(classRepository.findById("class1")).thenReturn(Optional.of(clase));
        when(assistanceRepository.existsByUserIdAndClassId("user1", "class1")).thenReturn(false);
        when(assistanceRepository.countByClassId("class1")).thenReturn(0L);

        assistanceService.completeInscription("user1", "class1");

        verify(assistanceRepository, times(2)).save(any(Assistance.class));
    }




}
