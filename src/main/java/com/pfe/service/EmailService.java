package com.pfe.service;

import com.pfe.model.VerificationCode;
import com.pfe.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public String generateVerificationCode(String email) {
        String code = generateRandomCode();
        VerificationCode verificationCode = VerificationCode.builder()
                .email(email)
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        verificationCodeRepository.save(verificationCode);
        return code;
    }
    public void sendVerificationEmail(String to) {
        String code = generateVerificationCode(to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@ehrassistant.com");
            helper.setTo(to);
            helper.setSubject("Code de vérification EHR Assistant");

            String content = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #0A2647;">Vérification de votre compte EHR Assistant</h2>
                    <p>Votre code de vérification est :</p>
                    <div style="background-color: #f5f5f5; padding: 15px; text-align: center; 
                              font-size: 24px; font-weight: bold; margin: 20px 0; border-radius: 5px;">
                        %s
                    </div>
                    <p>Ce code expirera dans 15 minutes.</p>
                    <p style="color: #666;">Si vous n'avez pas demandé ce code, veuillez ignorer cet email.</p>
                </div>
                """, code);

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: " + to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        return verificationCodeRepository
                .findByEmailAndCodeAndUsedFalseAndExpiryDateAfter(email, code, LocalDateTime.now())
                .map(verificationCode -> {
                    verificationCode.setUsed(true);
                    verificationCodeRepository.save(verificationCode);
                    return true;
                })
                .orElse(false);
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}