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
import java.util.ArrayList;
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

        testClass2 = new Class();
        testClass2.setId("67890");
        testClass2.setName("History 201");
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
    void createClass_shouldNotAddRepeatedSessions_whenNoSessionsAreProvided() {
        Class newClass = new Class();
        newClass.setId("123");

        when(classRepository.save(newClass)).thenReturn(newClass);

        Class result = classService.createClass(newClass);

        assertNotNull(result);
        assertNull(result.getSessions());
        verify(classRepository, times(1)).save(newClass);
    }


    @Test
    void deleteClass_shouldNotDelete_whenClassDoesNotExist() {
        String invalidId = "invalid";

        when(classRepository.existsById(invalidId)).thenReturn(false);

        boolean result = classService.deleteClass(invalidId);

        assertFalse(result);
        verify(classRepository, never()).deleteById(invalidId);
    }
    @Test
    void generateRepeatedSessions_shouldGenerateSessionsUntilEndDate() {
        Class baseClass = new Class();
        baseClass.setEndDate(LocalDate.of(2024, 1, 22));
        baseClass.setSessions(List.of(new Class.Session("1","Monday", "2024-01-01T10:00", "2024-01-01T11:00")));

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertEquals(3, result.size());
        assertEquals("2024-01-08T10:00", result.get(0).getStartTime());
        assertEquals("2024-01-15T10:00", result.get(1).getStartTime());
        assertEquals("2024-01-22T10:00", result.get(2).getStartTime());
    }

    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenNoSessionsProvided() {
        Class baseClass = new Class();
        baseClass.setEndDate(LocalDate.of(2024, 1, 22));
        baseClass.setSessions(List.of());

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenEndDateBeforeStartDate() {
        Class baseClass = new Class();
        baseClass.setEndDate(LocalDate.of(2023, 12, 31));
        baseClass.setSessions(List.of(new Class.Session("1","Monday", "2024-01-01T10:00", "2024-01-01T11:00")));

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertTrue(result.isEmpty());
    }
    @Test
    void getClassesByType_shouldReturnClassesOfSpecifiedType() {
        when(classRepository.findClassByType("Math")).thenReturn(List.of(testClass1));

        List<Class> result = classService.getClassesByType("Math");

        assertEquals(1, result.size());
        assertEquals("Math 101", result.get(0).getName());
        verify(classRepository, times(1)).findClassByType("Math");
    }

    @Test
    void getClassesByType_shouldReturnEmptyList_whenNoClassesOfSpecifiedTypeExist() {
        when(classRepository.findClassByType("Science")).thenReturn(List.of());

        List<Class> result = classService.getClassesByType("Science");

        assertTrue(result.isEmpty());
        verify(classRepository, times(1)).findClassByType("Science");
    }

    @Test
    void getClassesByType_shouldHandleNullTypeGracefully() {
        when(classRepository.findClassByType(null)).thenReturn(List.of());

        List<Class> result = classService.getClassesByType(null);

        assertTrue(result.isEmpty());
        verify(classRepository, times(1)).findClassByType(null);
    }
    @Test
    void createClass_shouldSaveClassWithRepeatedSessions_whenSessionsAreProvided() {
        Class newClass = new Class();
        newClass.setId("123");
        newClass.setSessions(List.of(new Class.Session("1","Monday", "2024-01-01T10:00", "2024-01-01T11:00")));
        newClass.setEndDate(LocalDate.of(2024, 1, 22));

        Class savedClass = new Class();
        savedClass.setId("123");
        savedClass.setSessions(new ArrayList<>(newClass.getSessions()));

        when(classRepository.save(newClass)).thenReturn(savedClass);
        when(classRepository.save(savedClass)).thenReturn(savedClass);

        Class result = classService.createClass(newClass);

        assertNotNull(result);
        assertEquals(4, result.getSessions().size()); // Original + 3 repeated sessions
        verify(classRepository, times(2)).save(any(Class.class));
    }

    @Test
    void createClass_shouldSaveClassWithoutSessions_whenNoSessionsAreProvided() {
        Class newClass = new Class();
        newClass.setId("123");

        when(classRepository.save(newClass)).thenReturn(newClass);

        Class result = classService.createClass(newClass);

        assertNotNull(result);
        assertNull(result.getSessions());
        verify(classRepository, times(1)).save(newClass);
    }

    @Test
    void createClass_shouldHandleNullSessionsGracefully() {
        Class newClass = new Class();
        newClass.setId("123");
        newClass.setSessions(null);

        when(classRepository.save(newClass)).thenReturn(newClass);

        Class result = classService.createClass(newClass);

        assertNotNull(result);
        assertNull(result.getSessions());
        verify(classRepository, times(1)).save(newClass);
    }
    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenBaseClassIsNull() {
        List<Class.Session> result = classService.generateRepeatedSessions(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenSessionsAreNull() {
        Class baseClass = new Class();
        baseClass.setSessions(null);

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenSessionsAreEmpty() {
        Class baseClass = new Class();
        baseClass.setSessions(List.of());

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateRepeatedSessions_shouldReturnEmptyList_whenEndDateIsNull() {
        Class baseClass = new Class();
        baseClass.setSessions(List.of(new Class.Session("1","Monday", "2024-01-01T10:00", "2024-01-01T11:00")));
        baseClass.setEndDate(null);

        List<Class.Session> result = classService.generateRepeatedSessions(baseClass);

        assertTrue(result.isEmpty());
    }


}