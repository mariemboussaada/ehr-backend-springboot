package com.pfe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "blacklisted_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    private String userEmail;

    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expirationDate;

    @Builder.Default
    private LocalDateTime blacklistedAt = LocalDateTime.now();

    // Méthode statique pour créer une instance facilement
    public static BlacklistedToken create(String token, String userEmail, LocalDateTime expirationDate) {
        return BlacklistedToken.builder()
                .token(token)
                .userEmail(userEmail)
                .expirationDate(expirationDate)
                .blacklistedAt(LocalDateTime.now())
                .build();
    }
}