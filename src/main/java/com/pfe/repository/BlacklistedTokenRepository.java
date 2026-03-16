package com.pfe.repository;

import com.pfe.model.BlacklistedToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedToken, String> {

    Optional<BlacklistedToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByUserEmail(String userEmail);
}

