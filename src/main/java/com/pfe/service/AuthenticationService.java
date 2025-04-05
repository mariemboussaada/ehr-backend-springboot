package com.pfe.service;

import com.pfe.dto.request.AuthenticationRequest;
import com.pfe.dto.request.RegisterRequest;
import com.pfe.dto.request.ResetPasswordRequest;
import com.pfe.dto.response.AuthenticationResponse;
import com.pfe.model.Doctor;
import com.pfe.repository.DoctorRepository;
import com.pfe.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final DoctorRepository doctorRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;



    public AuthenticationService(DoctorRepository doctorRepository, EmailService emailService, JwtService jwtService, PasswordEncoder passwordEncoder ) {
        this.doctorRepository = doctorRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var doctor = doctorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        if (!passwordEncoder.matches(request.getPassword(), doctor.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        var token = jwtService.generateToken(doctor);
        return AuthenticationResponse.builder()
                .token(token)
                .doctor(doctor)
                .build();
    }

    public void initiatePasswordReset(String email) {
        var doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));
        emailService.sendVerificationEmail(email);
    }

    public void resetPassword(String email, String code, String newPassword) {
        if (!emailService.verifyCode(email, code)) {
            throw new RuntimeException("Code de vérification invalide ou expiré");
        }

        var doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctorRepository.save(doctor);
    }


    public void initiateRegistration(RegisterRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
         emailService.sendVerificationEmail(request.getEmail());
    }

    public AuthenticationResponse completeRegistration(RegisterRequest request, String verificationCode) {
        if (!emailService.verifyCode(request.getEmail(), verificationCode)) {
            return AuthenticationResponse.builder()
                    .error("Code de vérification invalide ou expiré")
                    .build();
        }

        var doctor = Doctor.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .enabled(true)
                .build();

        doctorRepository.save(doctor);
        var token = jwtService.generateToken(doctor);

        return AuthenticationResponse.builder()
                .token(token)
                .doctor(doctor)
                .build();
    }





    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token ne peut pas être vide");
        }

        try {
            String email = jwtService.extractEmail(token);

            Doctor doctor = doctorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            System.out.println("Déconnexion de l'utilisateur : " + email);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la déconnexion", e);
        }
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        // Valider le nouveau mot de passe
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères");
        }

        // Encoder et enregistrer le nouveau mot de passe
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctorRepository.save(doctor);
    }


}