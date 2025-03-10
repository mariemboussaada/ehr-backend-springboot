package com.pfe.controller;

import com.pfe.dto.request.AuthenticationRequest;
import com.pfe.dto.request.RegisterRequest;
import com.pfe.dto.response.AuthenticationResponse;
import com.pfe.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTests {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    public void testInitiateRegistration() throws Exception {
       // when(authenticationService.initiateRegistration(any(RegisterRequest.class))).thenReturn("123456");

        mockMvc.perform(post("/api/auth/register/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Code de vérification envoyé à votre email"));
    }

    @Test
    public void testCompleteRegistration() throws Exception {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken("token123");

        when(authenticationService.completeRegistration(any(RegisterRequest.class), any(String.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}")
                        .param("verificationCode", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    public void testLogin() throws Exception {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken("token123");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    public void testInitiatePasswordReset() throws Exception {
        when(authenticationService.initiatePasswordReset(any(String.class))).thenReturn("123456");

        mockMvc.perform(post("/api/auth/reset-password/init")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Code de réinitialisation envoyé à votre email"));
    }

    @Test
    public void testCompletePasswordReset() throws Exception {
        doNothing().when(authenticationService).resetPassword(any(String.class), any(String.class), any(String.class));
        mockMvc.perform(post("/api/auth/reset-password/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"code\":\"123456\",\"newPassword\":\"newpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe réinitialisé avec succès"));
    }
}