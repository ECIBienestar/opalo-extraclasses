package com.example.edu.eci;

import com.example.edu.eci.model.Class;
import com.example.edu.eci.repository.ClassRepository;
import com.example.edu.eci.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private ClassService classService;

    private Class testClass1;
    private Class testClass2;
    private final String testId = "12345";

    @BeforeEach
    void setUp() {
        testClass1 = new Class();
        testClass1.setId(testId);
        testClass1.setName("Math 101");
        testClass1.setStartTime(LocalDateTime.now());

        testClass2 = new Class();
        testClass2.setId("67890");
        testClass2.setName("History 201");
        testClass2.setStartTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void getAllClasses() {
        when(classRepository.findAll()).thenReturn(Arrays.asList(testClass1, testClass2));

        List<Class> result = classService.getAllClasses();

        assertEquals(2, result.size());
        verify(classRepository, times(1)).findAll();
    }

    @Test
    void getClassById_Found() {
        when(classRepository.findById(testId)).thenReturn(Optional.of(testClass1));

        Optional<Class> result = classService.getClassById(testId);

        assertTrue(result.isPresent());
        verify(classRepository, times(1)).findById(testId);
    }

    @Test
    void getClassById_NotFound() {
        when(classRepository.findById("invalid")).thenReturn(Optional.empty());

        Optional<Class> result = classService.getClassById("invalid");

        assertFalse(result.isPresent());
    }

    @Test
    void createClass_shouldReturnListWithSingleClass_whenNoRepetition() {
        when(classRepository.save(testClass1)).thenReturn(testClass1);

        List<Class> result = classService.createClass(testClass1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testClass1, result.get(0));
        verify(classRepository, times(1)).save(testClass1);
    }


    @Test
    void updateClass_Success() {
        when(classRepository.existsById(testId)).thenReturn(true);
        when(classRepository.save(testClass1)).thenReturn(testClass1);

        Optional<Class> result = classService.updateClass(testId, testClass1);

        assertTrue(result.isPresent());
        verify(classRepository, times(1)).save(testClass1);
    }

    @Test
    void updateClass_Fail() {
        when(classRepository.existsById("invalid")).thenReturn(false);

        Optional<Class> result = classService.updateClass("invalid", testClass1);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteClass_Success() {
        when(classRepository.existsById(testId)).thenReturn(true);

        boolean result = classService.deleteClass(testId);

        assertTrue(result);
        verify(classRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteClass_Fail() {
        when(classRepository.existsById("invalid")).thenReturn(false);

        boolean result = classService.deleteClass("invalid");

        assertFalse(result);
    }

    @Test
    void getClassesByDateRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        when(classRepository.findByStartTimeBetween(start, end)).thenReturn(Arrays.asList(testClass1));

        List<Class> result = classService.getClassesByDateRange(start, end);

        assertEquals(1, result.size());
        verify(classRepository, times(1)).findByStartTimeBetween(start, end);
    }

    @Test
    void generateRepeatedClasses_weekly_repetition_should_create_correct_number() {
        Class baseClass = new Class();
        baseClass.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        baseClass.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        baseClass.setRepetition("weekly");
        baseClass.setEndTimeRepetition(LocalDateTime.of(2024, 1, 22, 10, 0));
        baseClass.setInstructorId("instructor123");

        List<Class> repeated = classService.generateRepeatedClasses(baseClass);

        assertEquals(3, repeated.size()); // 3 repeticiones: 8, 15, 22 de enero
        assertEquals(LocalDateTime.of(2024, 1, 8, 10, 0), repeated.get(0).getStartTime());
        assertEquals("instructor123", repeated.get(0).getInstructorId());
    }

    @Test
    void generateRepeatedClasses_should_return_empty_when_repetition_end_is_before_next_occurrence() {
        Class baseClass = new Class();
        baseClass.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        baseClass.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        baseClass.setRepetition("weekly");
        baseClass.setEndTimeRepetition(LocalDateTime.of(2024, 1, 2, 10, 0)); // no alcanza para repetir

        List<Class> repeated = classService.generateRepeatedClasses(baseClass);

        assertTrue(repeated.isEmpty(), "No se deberían generar clases repetidas");
    }


    @Test
    void testGetClassesByType_returnsMatchingClasses() {
        String type = "Deportiva";
        Class class1 = new Class();
        class1.setId("1");
        class1.setType(type);

        Class class2 = new Class();
        class2.setId("2");
        class2.setType(type);

        when(classRepository.findClassByType(type)).thenReturn(List.of(class1, class2));

        List<Class> result = classService.getClassesByType(type);

        assertEquals(2, result.size());
        assertEquals(type, result.get(0).getType());
        verify(classRepository).findClassByType(type);
    }

    @Test
    void generateRepeatedClasses_invalid_repetition_type_should_throw_exception() {
        Class baseClass = new Class();
        baseClass.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        baseClass.setEndTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        baseClass.setRepetition("daily");
        baseClass.setEndTimeRepetition(LocalDateTime.of(2024, 1, 10, 10, 0));

        assertThrows(IllegalArgumentException.class, () -> {
            classService.generateRepeatedClasses(baseClass);
        });
    }


}