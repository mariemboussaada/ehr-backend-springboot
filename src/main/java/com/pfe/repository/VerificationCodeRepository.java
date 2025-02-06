package com.pfe.repository;

import com.pfe.model.VerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
    Optional<VerificationCode> findByEmailAndCodeAndUsedFalseAndExpiryDateAfter(
            String email,
            String code,
            LocalDateTime now
    );
}