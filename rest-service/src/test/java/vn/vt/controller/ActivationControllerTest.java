package vn.vt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.vt.service.UserActivationService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ActivationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserActivationService userActivationService;

    @InjectMocks
    private ActivationController activationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(activationController).build();
    }

    @Test
    void testActivationSuccess() throws Exception {
        // Arrange
        String userId = "12345";
        when(userActivationService.activation(userId)).thenReturn(true);

        // Act
        mockMvc.perform(get("/api/user/activation")
                        .param("id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userActivationService, times(1)).activation(captor.capture());
        assertEquals(userId, captor.getValue());
    }

    @Test
    void testActivationFailure() throws Exception {
        // Arrange
        String userId = "12345";
        when(userActivationService.activation(userId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/user/activation")
                        .param("id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userActivationService, times(1)).activation(userId);
    }
}
