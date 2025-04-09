package com.fiitimprove.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiitimprove.backend.dto.PasswordResetDto;
import com.fiitimprove.backend.services.PasswordRecoveryService;

@RestController
@RequestMapping("/api/password")
public class PasswordRecoveryController {
    @Autowired
    private PasswordRecoveryService passRecover;

    @PostMapping("/recover/{email}")
    public ResponseEntity<?> passwordRecover(@PathVariable("email") String email) {
        passRecover.create(email);
        return ResponseEntity.ok("Mail was sent to your email");
    }

    @PostMapping("/check-code/{token}")
    public ResponseEntity<?> checkCode(@PathVariable String token) {
        return ResponseEntity.ok(passRecover.checkCode(token));
    }

    @PostMapping("/set-new-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordResetDto passReset) throws Exception {
        passRecover.changePassword(passReset.getToken(), passReset.getPassword());
        return ResponseEntity.ok("Password changed");
    }
}
