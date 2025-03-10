package com.pfe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Document(collection = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    private String id;

    private String userId;

    private LocalDateTime timestamp;

    private String topic;

    private List<Message> messages;

    // Getter pour le timestamp sous forme de String
    public String getTimestamp() {
        return timestamp == null ? null : timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String sender;
        private String content;
        private LocalDateTime sentAt;
    }
}
