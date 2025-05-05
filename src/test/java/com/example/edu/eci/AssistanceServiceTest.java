package com.example.edu.eci;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.service.AssistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

        Assistance assistance = new Assistance();
        assistance.setUserId(userId);
        assistance.setClassId(classId);
        assistance.setConfirm(false);

        when(assistanceRepository.findByUserIdAndClassId(userId, classId))
                .thenReturn(Optional.of(assistance));

        assertDoesNotThrow(() -> assistanceService.confirmAssistance(userId, classId));

        assertTrue(assistance.isConfirm());
        verify(assistanceRepository).save(assistance);
    }

    @Test
    void testConfirmAssistanceFailsWhenNotFound() {
        when(assistanceRepository.findByUserIdAndClassId("user1", "class1"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                assistanceService.confirmAssistance("user1", "class1"));
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
                assistanceService.confirmAssistance("user1", "class1"));
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
}
