package com.fiitimprove.backend.services;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;

import com.fiitimprove.backend.exceptions.AlreadyClosedException;
import com.fiitimprove.backend.exceptions.ResourceNotFoundException;
import com.fiitimprove.backend.models.PasswordRecovery;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.PasswordRecoveryRepository;
import com.fiitimprove.backend.repositories.UserRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for managing password recovery operations such as
 * generating recovery tokens, validating them, changing passwords,
 * and sending recovery emails.
 */
@Service
public class PasswordRecoveryService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRecoveryRepository passRecoverRep;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Creates a new password recovery token for the user with the specified email,
     * saves it in the database, and sends an email with the recovery code.
     * The token expires after 1 hour.
     *
     * @param email The email address of the user requesting password recovery.
     * @return The created PasswordRecovery object containing the token and expiration.
     * @throws ResourceNotFoundException if no user with the given email is found.
     */
    public PasswordRecovery create(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find user by email"));
        String token = String.format("%d", ThreadLocalRandom.current().nextLong());
        PasswordRecovery resetToken = new PasswordRecovery();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusHours(1));
        passRecoverRep.save(resetToken);
        this.sendEmail(email, "Password change", String.format("The code for password recovery: %s", token));
        return resetToken;
    }

    /**
     * Retrieves the PasswordRecovery entity associated with the given token.
     *
     * @param token The recovery token.
     * @return The PasswordRecovery object associated with the token.
     * @throws ResourceNotFoundException if the token does not exist.
     * @throws Exception if an unexpected error occurs.
     */
    private PasswordRecovery getFromToken(String token) throws Exception {
        return passRecoverRep.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find token"));
    }

    /**
     * Checks if the given password recovery token is valid and not expired.
     * If the token is expired, it will be deleted.
     *
     * @param token The recovery token to check.
     * @return true if the token exists and is not expired; false otherwise.
     */
    public boolean checkCode(String token) {
        try {
            PasswordRecovery pr = getFromToken(token);
            if (!LocalDateTime.now().isBefore(pr.getExpirationDate())) {
                passRecoverRep.delete(pr);
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Changes the password for the user associated with the given recovery token.
     * The token must be valid and not expired; otherwise, it throws an exception.
     *
     * @param token    The recovery token.
     * @param password The new password to set.
     * @return The updated User entity with the changed password.
     * @throws AlreadyClosedException if the token is expired or already used.
     * @throws Exception              if the token is not found or other errors occur.
     */
    public User changePassword(String token, String password) throws Exception {
        PasswordRecovery pr = getFromToken(token);
        if (!LocalDateTime.now().isBefore(pr.getExpirationDate())) {
            passRecoverRep.delete(pr);
            throw new AlreadyClosedException("Token expired or already exists");
        }
        User user = pr.getUser();
        user.setPassword(password);
        userRepository.save(user);
        return user;
    }

    /**
     * Sends an email with the specified recipient, subject, and body content.
     *
     * @param to      The recipient email address.
     * @param subject The email subject.
     * @param body    The body text of the email.
     */
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fitimprove.email.sender@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
