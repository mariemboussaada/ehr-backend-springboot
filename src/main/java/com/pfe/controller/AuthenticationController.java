package com.pfe.controller;

import com.pfe.dto.request.AuthenticationRequest;
import com.pfe.dto.request.RegisterRequest;
import com.pfe.dto.request.ResetPasswordRequest;
import com.pfe.dto.response.AuthenticationResponse;
import com.pfe.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/init")
    public ResponseEntity<String> initiateRegistration(@RequestBody RegisterRequest request) {
        try {
            String verificationCode = authenticationService.initiateRegistration(request);
            return ResponseEntity.ok("Code de vérification envoyé à votre email");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> completeRegistration(
            @RequestBody RegisterRequest request,
            @RequestParam String verificationCode) {
        try {
            AuthenticationResponse response = authenticationService.completeRegistration(request, verificationCode);
            if (response.getError() != null) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password/init")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String email) {
        try {
            authenticationService.initiatePasswordReset(email);
            return ResponseEntity.ok("Code de réinitialisation envoyé à votre email");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password/complete")
    public ResponseEntity<String> completePasswordReset(@RequestBody ResetPasswordRequest request) {
        try {
            authenticationService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}