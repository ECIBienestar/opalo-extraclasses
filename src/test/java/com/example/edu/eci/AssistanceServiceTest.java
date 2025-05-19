package com.example.edu.eci;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.repository.AssistanceRepository;
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

    @Mock
    private AssistanceRepository assistanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConfirmAssistanceSuccessfully() {
        String userId = "user1";
        String classId = "class1";
        String instructorId = "instructor1";

        Assistance assistance = new Assistance();
        assistance.setUserId(userId);
        assistance.setClassId(classId);
        assistance.setConfirm(false);

        when(assistanceRepository.findByUserIdAndClassId(userId, classId))
                .thenReturn(Optional.of(assistance));

        assertDoesNotThrow(() -> assistanceService.confirmAssistance(userId, classId,instructorId ));

        assertTrue(assistance.isConfirm());
        verify(assistanceRepository).save(assistance);
    }

    @Test
    void testConfirmAssistanceFailsWhenNotFound() {
        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "instructor1"));
    }

    @Test
    void testConfirmAssistanceFailsWhenAlreadyConfirmed() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setConfirm(true); // Ya confirmada

        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.of(assistance));

        assertThrows(IllegalStateException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "instructor1"));
    }
    @Test
    void getAssistancesWithTrueReturnsEmptyListWhenNoConfirmedAssistances() {
        when(assistanceRepository.findByConfirmTrue()).thenReturn(List.of());

        List<Assistance> result = assistanceService.getAssistancesWithTrue();

        assertTrue(result.isEmpty());
        verify(assistanceRepository).findByConfirmTrue();
    }

    @Test
    void getAssistancesWithTrueReturnsListOfConfirmedAssistances() {
        Assistance assistance1 = new Assistance();
        assistance1.setUserId("user1");
        assistance1.setClassId("class1");
        assistance1.setConfirm(true);

        Assistance assistance2 = new Assistance();
        assistance2.setUserId("user2");
        assistance2.setClassId("class2");
        assistance2.setConfirm(true);

        when(assistanceRepository.findByConfirmTrue()).thenReturn(List.of(assistance1, assistance2));

        List<Assistance> result = assistanceService.getAssistancesWithTrue();

        assertEquals(2, result.size());
        assertTrue(result.get(0).isConfirm());
        assertTrue(result.get(1).isConfirm());
        verify(assistanceRepository).findByConfirmTrue();
    }

    @Test
    void testCountAttendancesByClass_success() {
        String userId = "user1";
        String classId = "classA";
        int expectedCount = 3;

        when(assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId))
                .thenReturn(expectedCount);

        long result = assistanceService.countAttendancesByClass(userId, classId);
        assertEquals(expectedCount, result);
        verify(assistanceRepository).countByUserIdAndClassIdAndConfirmTrue(userId, classId);
    }

    @Test
    void testCountAttendancesByClass_noAttendances_throwsException() {
        String userId = "user2";
        String classId = "classA";

        when(assistanceRepository.countByUserIdAndClassIdAndConfirmTrue(userId, classId))
                .thenReturn(0);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                assistanceService.countAttendancesByClass(userId, classId));

        assertEquals("No hay asistencias registradas", exception.getMessage());
        verify(assistanceRepository).countByUserIdAndClassIdAndConfirmTrue(userId, classId);
    }

    @Test
    void getAssistancesWithFalseBefore_returnsUnconfirmedAndPastAssistances() {
        Assistance a1 = new Assistance();
        a1.setUserId("user1");
        a1.setClassId("class1");
        a1.setConfirm(false);
        a1.setStartTime(LocalDateTime.now().minusDays(1));

        Assistance a2 = new Assistance();
        a2.setUserId("user2");
        a2.setClassId("class2");
        a2.setConfirm(false);
        a2.setStartTime(LocalDateTime.now().minusHours(2));

        List<Assistance> expected = List.of(a1, a2);

        when(assistanceRepository.findByConfirmFalseAndStartTimeIsBefore(any(LocalDateTime.class)))
                .thenReturn(expected);

        List<Assistance> result = assistanceService.getAssistancesWithFalseBefore();

        assertEquals(2, result.size());
        assertFalse(result.get(0).isConfirm());
        assertFalse(result.get(1).isConfirm());
        verify(assistanceRepository).findByConfirmFalseAndStartTimeIsBefore(any(LocalDateTime.class));
    }
    @Test
    void confirmAssistance_shouldThrowException_whenUserNotEnrolledInClass() {
        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "instructor1"));
    }

    @Test
    void confirmAssistance_shouldThrowException_whenAssistanceAlreadyConfirmed() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setConfirm(true);

        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.of(assistance));

        assertThrows(IllegalStateException.class, () ->
                assistanceService.confirmAssistance("user1", "class1", "instructor1"));
    }

    @Test
    void confirmAssistance_shouldConfirmAssistanceSuccessfully() {
        Assistance assistance = new Assistance();
        assistance.setUserId("user1");
        assistance.setClassId("class1");
        assistance.setConfirm(false);

        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.of(assistance));

        assistanceService.confirmAssistance("user1", "class1", "instructor1");

        assertTrue(assistance.isConfirm());
        assertEquals("instructor1", assistance.getInstructorId());
        verify(assistanceRepository).save(assistance);
    }

    @Test
    void countConfirmedAttendances_shouldReturnCount_whenAttendancesExist() {
        String userId = "user1";
        LocalDate start = LocalDate.of(2024, 7, 1);
        LocalDate end = LocalDate.of(2024, 7, 31);

        when(assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX)))
                .thenReturn(5);

        long result = assistanceService.countConfirmedAttendances(userId, start, end);

        assertEquals(5L, result);
        verify(assistanceRepository).countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    @Test
    void countConfirmedAttendances_shouldThrowException_whenNoAttendancesExist() {
        String userId = "user2";
        LocalDate start = LocalDate.of(2024, 7, 1);
        LocalDate end = LocalDate.of(2024, 7, 31);

        when(assistanceRepository.countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX)))
                .thenReturn(0);

        Exception exception = assertThrows(IllegalStateException.class, () ->
                assistanceService.countConfirmedAttendances(userId, start, end));

        assertEquals("No hay asistencias registradas para este usuario", exception.getMessage());
        verify(assistanceRepository).countByUserIdAndConfirmTrueAndStartTimeBetween(
                userId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }


}
