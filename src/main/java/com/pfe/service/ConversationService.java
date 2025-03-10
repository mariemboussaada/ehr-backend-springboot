package com.pfe.service;

import com.pfe.model.Conversation;
import com.pfe.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public Conversation saveConversation(Conversation conversation) {
        // Définir le timestamp lors de la sauvegarde
        if (conversation.getTimestamp() == null) {
            conversation.setTimestamp(LocalDateTime.now());
        }
        return conversationRepository.save(conversation);
    }


    public List<Conversation> getUserConversations(String userId) {
        // Récupérer toutes les conversations pour un utilisateur donné
        return conversationRepository.findByUserId(userId);
    }

    public Optional<Conversation> getConversationById(String id, String userId) {
        // Récupérer une conversation spécifique pour un utilisateur
        return Optional.ofNullable(
                conversationRepository.findByIdAndUserId(id, userId)
        );
    }

    public List<Conversation> getConversationsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        // Récupérer les conversations dans une plage de dates
        return conversationRepository.findByTimestampBetween(startDate, endDate);
    }

    public void deleteConversation(String id) {
        // Supprimer une conversation par son ID
        conversationRepository.deleteById(id);
    }

    // Méthode supplémentaire pour ajouter un message à une conversation existante
    public Conversation addMessageToConversation(String conversationId, Conversation.Message message) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Ajouter le message à la conversation
        conversation.getMessages().add(message);

        return conversationRepository.save(conversation);
    }
    public void deleteAllConversations() {
        conversationRepository.deleteAll();
    }
    public Conversation updateConversation(String id, Conversation updatedConversation) {
        // Vérifier que la conversation existe
        Conversation existingConversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation non trouvée avec l'id: " + id));

        // S'assurer que l'ID reste le même
        updatedConversation.setId(id);

        // Mettre à jour le timestamp si nécessaire
        if (updatedConversation.getTimestamp() == null) {
            updatedConversation.setTimestamp(LocalDateTime.now());
        }

        // Sauvegarder les changements
        return conversationRepository.save(updatedConversation);
    }
}