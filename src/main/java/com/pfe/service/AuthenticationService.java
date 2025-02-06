package com.pfe.service;

import com.pfe.dto.request.AuthenticationRequest;
import com.pfe.dto.request.RegisterRequest;
import com.pfe.dto.response.AuthenticationResponse;
import com.pfe.model.Doctor;
import com.pfe.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final DoctorRepository doctorRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationService(DoctorRepository doctorRepository, EmailService emailService, JwtService jwtService, PasswordEncoder passwordEncoder) {
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

    public String initiatePasswordReset(String email) {
        var doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));
        return emailService.generateVerificationCode(email);
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


    public String initiateRegistration(RegisterRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return emailService.generateVerificationCode(request.getEmail());
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

    public String initiateLogin(String email) {
        if (!doctorRepository.existsByEmail(email)) {
            throw new RuntimeException("Email non trouvé");
        }
        return emailService.generateVerificationCode(email);
    }

    public AuthenticationResponse completeLogin(AuthenticationRequest request) {
        var doctor = doctorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Doctor non trouvé"));

        if (!emailService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            return AuthenticationResponse.builder()
                    .error("Code de vérification invalide ou expiré")
                    .build();
        }

        var token = jwtService.generateToken(doctor);
        return AuthenticationResponse.builder()
                .token(token)
                .doctor(doctor)
                .build();
    }
}