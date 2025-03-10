package com.pfe.repository;

import com.pfe.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByUserId(String userId);
    Conversation findByIdAndUserId(String id, String userId);
    List<Conversation> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}