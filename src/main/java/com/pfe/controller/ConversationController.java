package com.pfe.controller;

import com.pfe.model.Conversation;
import com.pfe.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    // Créer une nouvelle conversation
    @PostMapping
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        Conversation savedConversation = conversationService.saveConversation(conversation);
        System.out.println("Conversation reçue: " + conversation);
        return ResponseEntity.ok(savedConversation);
    }

    // Ajouter un message à une conversation existante
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Conversation> addMessageToConversation(
            @PathVariable String conversationId,
            @RequestBody Conversation.Message message
    ) {
        Conversation updatedConversation = conversationService.addMessageToConversation(conversationId, message);
        return ResponseEntity.ok(updatedConversation);
    }

    // Récupérer les conversations d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Conversation>> getUserConversations(@PathVariable String userId) {
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // Récupérer une conversation spécifique pour un utilisateur
    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversation(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        return conversationService.getConversationById(id, userId)
                .map(conversation -> {
                    // Tri des messages par date
                    conversation.setMessages(conversation.getMessages().stream()
                            .sorted(Comparator.comparing(Conversation.Message::getSentAt))
                            .collect(Collectors.toList()));
                    return ResponseEntity.ok(conversation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Récupérer les conversations dans une plage de dates
    @GetMapping("/date-range")
    public ResponseEntity<List<Conversation>> getConversationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<Conversation> conversations = conversationService.getConversationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(conversations);
    }

    // Supprimer une conversation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }
    // Supprimer toutes les conversations
    @DeleteMapping
    public ResponseEntity<Void> deleteAllConversations() {
        conversationService.deleteAllConversations();
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Conversation> updateConversation(
            @PathVariable String id,
            @RequestBody Conversation conversation) {
        try {
            Conversation updatedConversation = conversationService.updateConversation(id, conversation);
            return ResponseEntity.ok(updatedConversation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}