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


}
