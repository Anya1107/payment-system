package com.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.config.PostgreSQLTestContainerConfig;
import com.userservice.dto.*;
import com.userservice.util.TestDataCreator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Import(PostgreSQLTestContainerConfig.class)
public class UserControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void success_registration() throws Exception {
        String email = "user@example.com";
        UserRegistrationRequest request = TestDataCreator.buildRegistrationRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/user")
                        .param("email", email))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("firstName"));
    }

    @Test
    void registrationFail_existingUser_returnConflict() throws Exception {
        String email = "duplicate@example.com";
        UserRegistrationRequest request = TestDataCreator.buildRegistrationRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists with email: " + email));
    }

    @Test
    void success_update() throws Exception {
        String email = "update@example.com";
        UserRegistrationRequest request = TestDataCreator.buildRegistrationRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        UserDto userDto = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/user").param("email", email))
                        .andReturn().getResponse().getContentAsString(), UserDto.class
        );

        UserUpdateRequest updateRequest = new UserUpdateRequest();

        updateRequest.setEmail("new@example.com");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setFilled(true);

        mockMvc.perform(put("/api/v1/user/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/user/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateFail_notExistingUser_returnNotFoundException() throws Exception {
        String email = "update@example.com";
        UserRegistrationRequest request = TestDataCreator.buildRegistrationRequest(email);

        mockMvc.perform(put("/api/v1/user/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void success_delete() throws Exception {
        String email = "delete@example.com";
        UserRegistrationRequest request = TestDataCreator.buildRegistrationRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        UserDto userDto = objectMapper.readValue(
                mockMvc.perform(get("/api/v1/user")
                                .param("email", email))
                        .andReturn().getResponse().getContentAsString(),
                UserDto.class
        );

        mockMvc.perform(delete("/api/v1/user/" + userDto.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/user/" + userDto.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFail_notExistingUser_returnNotFoundException() throws Exception {
        mockMvc.perform(delete("/api/v1/user/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}