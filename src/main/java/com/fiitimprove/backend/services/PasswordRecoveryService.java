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

@Service
public class PasswordRecoveryService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRecoveryRepository passRecoverRep;
    @Autowired
    private JavaMailSender mailSender;

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

    private PasswordRecovery getFromToken(String token) throws Exception {
        return passRecoverRep.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Could not find token"));
    }

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

    public void changePassword(String token, String password) throws Exception {
        PasswordRecovery pr = getFromToken(token);
        if (!LocalDateTime.now().isBefore(pr.getExpirationDate())) {
            passRecoverRep.delete(pr);
            throw new AlreadyClosedException("Token expired or already exists");
        }
        User user = pr.getUser();
        user.setPassword(password);
        userRepository.save(user);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fitimprove.email.sender@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
