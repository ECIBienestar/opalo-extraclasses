package com.example.edu.eci;

import com.example.edu.eci.model.Assistance;
import com.example.edu.eci.repository.AssistanceRepository;
import com.example.edu.eci.service.AssistanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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
        assistance.setConfirm(false); // No confirmada aún

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
}
