package com.pfe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "verification_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {
    @Id
    private String id;

    @Indexed(expireAfterSeconds = 900) // 15 minutes
    private String email;

    private String code;

    @Builder.Default
    private LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

    @Builder.Default
    private boolean used = false;

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}