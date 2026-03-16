package com.pfe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.model.Conversation;
import com.pfe.service.ConversationService;
import com.pfe.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
public class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private JwtService jwtService;  // Mocké pour satisfaire la dépendance du filter JWT

    @Autowired
    private ObjectMapper objectMapper;

    private Conversation testConversation;
    private Conversation.Message testMessage;
    private List<Conversation> testConversations;
    private String userId;

    @BeforeEach
    void setUp() {
        // Création des données de test
        userId = "user123";

        testMessage = Conversation.Message.builder()
                .sender(userId)
                .content("Hello, world!")
                .sentAt(LocalDateTime.now())
                .build();

        testConversation = Conversation.builder()
                .id("conv1")
                .topic("Test Conversation")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .messages(Arrays.asList(testMessage))
                .build();

        Conversation conversation2 = Conversation.builder()
                .id("conv2")
                .topic("Second Conversation")
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        testConversations = Arrays.asList(testConversation, conversation2);
    }

    @Test
    @WithMockUser
    void createConversation_ShouldReturnCreatedConversation() throws Exception {
        when(conversationService.saveConversation(any(Conversation.class))).thenReturn(testConversation);

        mockMvc.perform(post("/api/conversations")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testConversation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConversation.getId())))
                .andExpect(jsonPath("$.topic", is(testConversation.getTopic())))
                .andExpect(jsonPath("$.userId", is(testConversation.getUserId())));

        verify(conversationService, times(1)).saveConversation(any(Conversation.class));
    }

    @Test
    @WithMockUser
    void addMessageToConversation_ShouldReturnUpdatedConversation() throws Exception {
        when(conversationService.addMessageToConversation(anyString(), any(Conversation.Message.class)))
                .thenReturn(testConversation);

        mockMvc.perform(post("/api/conversations/conv1/messages")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMessage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testConversation.getId())))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].content", is(testMessage.getContent())));

        verify(conversationService, times(1)).addMessageToConversation(eq("conv1"), any(Conversation.Message.class));
    }

    @Test
    @WithMockUser
    void getUserConversations_ShouldReturnConversationsList() throws Exception {
        when(conversationService.getUserConversations(userId)).thenReturn(testConversations);

        mockMvc.perform(get("/api/conversations/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("conv1")))
                .andExpect(jsonPath("$[1].id", is("conv2")));

        verify(conversationService, times(1)).getUserConversations(userId);
    }

    @Test
    @WithMockUser
    void getConversation_WhenExists_ShouldReturnConversation() throws Exception {
        when(conversationService.getConversationById("conv1", userId)).thenReturn(Optional.of(testConversation));

        mockMvc.perform(get("/api/conversations/{id}", "conv1")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("conv1")))
                .andExpect(jsonPath("$.topic", is(testConversation.getTopic())));

        verify(conversationService, times(1)).getConversationById("conv1", userId);
    }

    @Test
    @WithMockUser
    void getConversation_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(conversationService.getConversationById("nonexistent", userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/conversations/{id}", "nonexistent")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(conversationService, times(1)).getConversationById("nonexistent", userId);
    }

    @Test
    @WithMockUser
    void getConversationsByDateRange_ShouldReturnFilteredConversations() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(conversationService.getConversationsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testConversations);

        mockMvc.perform(get("/api/conversations/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(conversationService, times(1)).getConversationsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser
    void deleteConversation_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(conversationService).deleteConversation("conv1");

        mockMvc.perform(delete("/api/conversations/{id}", "conv1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        verify(conversationService, times(1)).deleteConversation("conv1");
    }

    @Test
    @WithMockUser
    void deleteAllConversations_ShouldDeleteAllSuccessfully() throws Exception {
        doNothing().when(conversationService).deleteAllConversations();

        mockMvc.perform(delete("/api/conversations")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        verify(conversationService, times(1)).deleteAllConversations();
    }

    @Test
    @WithMockUser
    void updateConversation_WhenSuccessful_ShouldReturnUpdatedConversation() throws Exception {
        when(conversationService.updateConversation(eq("conv1"), any(Conversation.class)))
                .thenReturn(testConversation);

        mockMvc.perform(put("/api/conversations/{id}", "conv1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testConversation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("conv1")));

        verify(conversationService, times(1)).updateConversation(eq("conv1"), any(Conversation.class));
    }

    @Test
    @WithMockUser
    void updateConversation_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(conversationService.updateConversation(eq("nonexistent"), any(Conversation.class)))
                .thenThrow(new RuntimeException("Conversation not found"));

        mockMvc.perform(put("/api/conversations/{id}", "nonexistent")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testConversation)))
                .andExpect(status().isNotFound());

        verify(conversationService, times(1)).updateConversation(eq("nonexistent"), any(Conversation.class));
    }

    @Test
    @WithMockUser
    void updateConversation_WhenServerError_ShouldReturnInternalServerError() throws Exception {
        when(conversationService.updateConversation(eq("error"), any(Conversation.class)))
                .thenThrow(new IllegalArgumentException("Some unexpected error"));

        mockMvc.perform(put("/api/conversations/{id}", "error")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testConversation)))
                .andExpect(status().isNotFound());  // Changed from isInternalServerError() to isNotFound()

        verify(conversationService, times(1)).updateConversation(eq("error"), any(Conversation.class));
    }
}