package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.dto.PasswordResetDto;
import com.fiitimprove.backend.security.SecurityUtil;
import com.fiitimprove.backend.services.PasswordRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordRecoveryController {

    @Autowired
    private SecurityUtil securityUtil;
    private final PasswordRecoveryService passRecover;

    public PasswordRecoveryController(PasswordRecoveryService passRecover) {
        this.passRecover = passRecover;
    }

    @PostMapping("/recover/{email}")
    @Operation(summary = "Initiate password recovery", description = "Sends a password recovery email to the specified email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recovery email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email"),
            @ApiResponse(responseCode = "404", description = "User with this email not found")
    })
    public ResponseEntity<?> passwordRecover(@PathVariable("email") String email) {
        passRecover.create(email);
        return ResponseEntity.ok("Mail was sent to your email");
    }

    @PostMapping("/check-code/{token}")
    @Operation(summary = "Check password recovery token", description = "Verifies the password recovery token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "400", description = "Invalid token"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    public ResponseEntity<?> checkCode(@PathVariable String token) {
        return ResponseEntity.ok(passRecover.checkCode(token));
    }

    @PostMapping("/set-new-password")
    @Operation(summary = "Set new password", description = "Changes the user's password using the recovery token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token or password"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    public ResponseEntity<?> changePassword(@RequestBody PasswordResetDto passReset) throws Exception {
        passRecover.changePassword(passReset.getToken(), passReset.getPassword());
        return ResponseEntity.ok("Password changed");
    }
}