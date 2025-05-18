package com.fiitimprove.backend.services;

import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.PasswordRecovery;
import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.PasswordRecoveryRepository;
import com.fiitimprove.backend.repositories.UserRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PasswordRecoveryService}.
 * 
 * <p>This class tests the password recovery flow including creation
 * of recovery tokens, validation of recovery codes, and password updates.</p>
 */
public class PasswordRecoveryTest {
    @InjectMocks
    private PasswordRecoveryService recoveryService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordRecoveryRepository recoveryRepository;
    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the complete password recovery process:
     * <ul>
     *   <li>Creation of a password recovery token for a given user email.</li>
     *   <li>Verification that the generated token is valid and can be checked.</li>
     *   <li>Handling the case when an invalid token is used to change the password.</li>
     *   <li>Successful password change using a valid recovery token.</li>
     * </ul>
     * 
     * @throws Exception if any step in the recovery process fails
     */
    @Test
    public void testRecovery() throws Exception {
        String email = "something@example.com";
        String password = "Something1";
        var user = new RegularUser();
        user.setEmail(email);
        user.setId(10L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(recoveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PasswordRecovery result = recoveryService.create(email);
        assertNotNull(result);
        assertEquals(user, result.getUser());
        
        when(recoveryRepository.findByToken(result.getToken())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            PasswordRecovery recovery = new PasswordRecovery();
            recovery.setToken(token.trim());
            recovery.setUser(user);
            recovery.setExpirationDate(LocalDateTime.now().plusMinutes(30));
            return Optional.of(recovery);
        });
        assertTrue(recoveryService.checkCode(result.getToken()));

        assertThrows(ResourceNotFoundException.class, () -> 
            recoveryService.changePassword(result.getToken() + "d", password)
        );

        User _user = recoveryService.changePassword(result.getToken(), password);
        assertEquals(_user.getPassword(), password, "After password change user does not have a correct password");
    }
}
