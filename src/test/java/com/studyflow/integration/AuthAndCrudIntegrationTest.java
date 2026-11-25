package com.studyflow.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginAndSubjectCrudFlow() throws Exception {
        String email = "flow_" + System.currentTimeMillis() + "@example.com";

        String registerBody = """
                {
                  "fullName": "Flow User",
                  "email": "%s",
                  "password": "password123",
                  "confirmPassword": "password123",
                  "educationLevel": "Undergraduate"
                }
                """.formatted(email);

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email))
                .andReturn();

        String token = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("token").asText();

        String loginBody = """
                {
                  "email": "%s",
                  "password": "password123"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Flow User"));

        String subjectBody = """
                {
                  "name": "Algorithms",
                  "code": "CS301",
                  "description": "Design and analysis",
                  "targetHoursPerWeek": 6.0,
                  "color": "#7C3AED"
                }
                """;

        MvcResult createSubject = mockMvc.perform(post("/api/subjects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Algorithms"))
                .andReturn();

        JsonNode subject = objectMapper.readTree(createSubject.getResponse().getContentAsString());
        long subjectId = subject.get("id").asLong();
        assertThat(subjectId).isPositive();

        mockMvc.perform(get("/api/subjects")
                        .header("Authorization", "Bearer " + token)
                        .param("q", "Algo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Algorithms"));

        mockMvc.perform(put("/api/subjects/" + subjectId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Algorithms II",
                                  "code": "CS302",
                                  "description": "Advanced",
                                  "targetHoursPerWeek": 8.0,
                                  "color": "#6366F1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Algorithms II"));

        String taskBody = """
                {
                  "subjectId": %d,
                  "title": "Finish problem set",
                  "description": "Chapter 4",
                  "priority": "HIGH",
                  "status": "PENDING"
                }
                """.formatted(subjectId);

        MvcResult createTask = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Finish problem set"))
                .andReturn();

        long taskId = objectMapper.readTree(createTask.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(patch("/api/tasks/" + taskId + "/complete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Flow User"))
                .andExpect(jsonPath("$.totalSubjects").value(1));
    }
}
