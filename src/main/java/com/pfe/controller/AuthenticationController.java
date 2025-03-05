package com.pfe.controller;

import com.pfe.dto.request.AuthenticationRequest;
import com.pfe.dto.request.RegisterRequest;
import com.pfe.dto.request.ResetPasswordRequest;
import com.pfe.dto.response.AuthenticationResponse;
import com.pfe.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/register")
    public ResponseEntity<String> initiateRegistration(@RequestBody RegisterRequest request) {
        try {
            authenticationService.initiateRegistration(request);
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authenticationService.logout(token);
            }
            return ResponseEntity.ok("Déconnexion réussie");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la déconnexion : " + e.getMessage());
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

    @PostMapping("/reset-password")
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

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = authenticationService.checkEmailExists(email);
            if (!exists) {
                return ResponseEntity.ok().body("Adresse email introuvable");
            }
            return ResponseEntity.ok().body("Email valide");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ResetPasswordRequest request,
            Principal principal) {
        try {
            authenticationService.changePassword(
                    principal.getName(),  // Email de l'utilisateur connecté
                    request.getOldPassword(),  // Ancien mot de passe
                    request.getNewPassword()   // Nouveau mot de passe
            );
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}