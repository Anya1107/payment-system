package com.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Test
    void success_registration() throws Exception {
        String email = "user@example.com";
        UserRegistrationRequest request = buildValidRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/user")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value("firstName"));
    }

    @Test
    void registrationFail_existingUser_returnConflict() throws Exception {
        String email = "duplicate@example.com";
        UserRegistrationRequest request = buildValidRequest(email);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists with email: " + email));
    }

    @Test
    void success_update() throws Exception {
        String email = "update@example.com";
        UserRegistrationRequest request = buildValidRequest(email);

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
        UserRegistrationRequest request = buildValidRequest("update@example.com");

        mockMvc.perform(put("/api/v1/user/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void success_delete() throws Exception {
        String email = "delete@example.com";
        UserRegistrationRequest request = buildValidRequest(email);

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

    public static UserRegistrationRequest buildValidRequest(String email) {
        return new UserRegistrationRequest()
                .user(
                        new UserCreateRequest()
                                .email(email)
                                .firstName("firstName")
                                .lastName("lastName")
                                .secretKey("secretKey")
                )
                .address(
                        new AddressCreateRequest()
                                .address("address")
                                .city("city")
                                .countryId(1)
                                .state("state")
                                .zipCode("zipCode")
                )
                .individual(
                        new IndividualCreateRequest()
                                .passportNumber("12345678")
                                .phoneNumber("32983298")
                                .status("active")
                );
    }
}