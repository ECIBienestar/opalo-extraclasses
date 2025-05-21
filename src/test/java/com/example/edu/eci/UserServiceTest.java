package com.example.edu.eci;
import com.example.edu.eci.model.User;
        import com.example.edu.eci.repository.UserRepository;
        import com.example.edu.eci.service.UserService;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.mockito.InjectMocks;
        import org.mockito.Mock;
        import org.mockito.MockitoAnnotations;

        import java.util.Optional;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

        public class UserServiceTest {

            @Mock
            private UserRepository userRepository;

            @InjectMocks
            private UserService userService;

            @BeforeEach
            void setUp() {
                MockitoAnnotations.openMocks(this);
            }

            @Test
            void getUserByIdShouldReturnUserWhenUserExists() {
                User user = new User();
                user.setId("user1");

                when(userRepository.findById("user1")).thenReturn(Optional.of(user));

                Optional<User> result = userService.getUserById("user1");

                assertTrue(result.isPresent());
                assertEquals("user1", result.get().getId());
                verify(userRepository).findById("user1");
            }

            @Test
            void getUserByIdShouldReturnEmptyWhenUserDoesNotExist() {
                when(userRepository.findById("user1")).thenReturn(Optional.empty());

                Optional<User> result = userService.getUserById("user1");

                assertFalse(result.isPresent());
                verify(userRepository).findById("user1");
            }

            @Test
            void createUserShouldSaveAndReturnUser() {
                User newUser = new User();
                newUser.setId("user1");

                when(userRepository.save(newUser)).thenReturn(newUser);

                User result = userService.createUser(newUser);

                assertNotNull(result);
                assertEquals("user1", result.getId());
                verify(userRepository).save(newUser);
            }
        }