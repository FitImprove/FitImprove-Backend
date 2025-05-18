package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.IncorrectDataException;
import com.fiitimprove.backend.models.*;
import com.fiitimprove.backend.repositories.UserRepository;
import com.fiitimprove.backend.requests.UserUpdateProfileRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserService} class
 * <p>Tested scenarios include:</p>
 * <ul>
 *     <li>Successful signup with a regular user</li>
 *     <li>Signup with an existing email</li>
 *     <li>Signup with an insecure password</li>
 *     <li>Successful profile update</li>
 *     <li>Profile update for a non-existing user</li>
 * </ul> 
 */
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SettingsService settingsService;
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Initializes the mocks before each test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests user signup with a valid regular user.
     * Verifies that the user is saved and settings are created.
     */
    @Test
    public void testSignup_withRegularUser_shouldSaveUserAndSettings() {
        RegularUser user = new RegularUser();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("rawPassword1");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            ((User) invocation.getArgument(0)).setId(1L);
            return invocation.getArgument(0);
        }).when(userRepository).save(any(User.class));

        User savedUser = userService.signup(user);

        verify(userRepository, times(2)).save(any(User.class));
        verify(settingsService).createSettings(eq(1L), any(Settings.class));
        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName());
    }

    /**
     * Tests signup when the user email already exists.
     */
    @Test
    public void testSignup_withExistingEmail_shouldThrowException() {
        RegularUser user = new RegularUser();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IncorrectDataException.class, () -> userService.signup(user));

        assertEquals("This email has already exist", exception.getMessage());
    }

    /**
     * Tests signup with a password that doesn't meet security requirements.
     * Expects an exception to be thrown (actual type should be clarified in implementation).
     */
    @Test
    public void testSignup_withRegularUser_notSecurePassword() {
        RegularUser user = new RegularUser();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("rawPassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            ((User) invocation.getArgument(0)).setId(1L);
            return invocation.getArgument(0);
        }).when(userRepository).save(any(User.class));

        assertThrows(Exception.class, () -> userService.signup(user));
    }

    /**
     * Tests updating a user profile with valid data.
     * Verifies that user fields are updated as expected.
     *
     * @throws Exception if update fails due to internal service logic
     */
    @Test
    public void testUpdateUser_withValidData_shouldUpdateUser() throws Exception {
        Long userId = 1L;
        Coach coach = new Coach();
        coach.setId(userId);
        coach.setUsername("oldusername");
        coach.setName("Old Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(coach));
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setUsername("newusername");
        request.setName("New Name");
        request.setGender(User.Gender.MALE);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1).toString());
        request.setFields(List.of("Fitness"));
        request.setSkills(List.of("Yoga"));
        request.setSelfIntroduction("I am a coach");
        request.setWorksInFieldSince(LocalDate.of(2015, 1, 1));

        User updatedUser = userService.updateUser(userId, request);

        assertEquals("newusername", updatedUser.getUsername());
        assertEquals("New Name", updatedUser.getName());
        assertEquals(User.Gender.MALE, updatedUser.getGender());
        assertEquals(List.of("Fitness"), ((Coach) updatedUser).getFields());
        assertEquals(List.of("Yoga"), ((Coach) updatedUser).getSkills());
    }

    /**
     * Tests updating a user profile when the user ID does not exist.
     * Expects an {@link IllegalArgumentException} to be thrown.
     */
    @Test
    public void testUpdateUser_withNonExistingId_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(1L, new UserUpdateProfileRequest());
        });

        assertEquals("User with ID 1 not found", exception.getMessage());
    }
}
