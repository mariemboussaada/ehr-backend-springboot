package com.pfe.service;

import com.pfe.model.BlacklistedToken;
import com.pfe.repository.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
@Service
public class TokenBlacklistService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Ajouter un token à la blacklist
     */
    public void blacklistToken(String token, String userEmail) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Le token ne peut pas être vide");
        }

        // Extraire la date d'expiration du token
        Date expiration = jwtService.extractExpiration(token);
        LocalDateTime expirationDateTime = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        BlacklistedToken blacklistedToken = BlacklistedToken.create(token, userEmail, expirationDateTime);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    /**
     * Vérifier si un token est blacklisté
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return blacklistedTokenRepository.existsByToken(token);
    }

    /**
     * Supprimer tous les tokens blacklistés d'un utilisateur
     */
    public void removeUserTokensFromBlacklist(String userEmail) {
        blacklistedTokenRepository.deleteByUserEmail(userEmail);
    }

    /**
     * Nettoyer les tokens expirés (appelée périodiquement)
     */
    public void cleanupExpiredTokens() {
        // MongoDB s'occupe automatiquement du nettoyage grâce à l'index TTL
        // Cette méthode peut être utilisée pour un nettoyage manuel si nécessaire
    }
}