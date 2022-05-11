package com.f2cm.eventmanager.presentation.api.events;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class TimeSlotRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(delete("/api/v1/time-slots"));
        mockMvc.perform(delete("/api/v1/events"));
    }

    @Test
    public void ensureThatCreatingAndRemovingTimeSlotsWorks() throws Exception {
        var createResponse1 = mockMvc.perform(
                        post("/api/v1/time-slots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"from\": \"2022/01/01 20:00:00\",\n" +
                                        "  \"to\": \"2022/01/01 20:30:00\",\n" +
                                        "  \"name\": \"opening\",\n" +
                                        "  \"description\": \"123\",\n" +
                                        "  " +
                                        "\"contactToken\": null,\n" +
                                        "  \"eventToken\": null\n" +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andReturn();
        String token1 = JsonPath.read(createResponse1.getResponse().getContentAsString(), "$._token");

        var createResponse2 = mockMvc.perform(
                        post("/api/v1/time-slots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"from\": \"2022/01/02 00:00:00\",\n" +
                                        "  \"to\": \"2022/01/01 00:30:00\",\n" +
                                        "  \"name\": \"Midnight-Special\",\n" +
                                        "  \"description\": null,\n" +
                                        "  " +
                                        "\"contactToken\": null,\n" +
                                        "  \"eventToken\": null\n" +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andReturn();
        String token2 = JsonPath.read(createResponse2.getResponse().getContentAsString(), "$._token");

        mockMvc.perform(get("/api/v1/time-slots"))
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(delete("/api/v1/time-slots/%s".formatted(token1)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/time-slots"))
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    public void ensureThatCreatingTimeSlotsWithEventsWorks() throws Exception {
        var req1 = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"Switch XXL\",\n" +
                                "  \"startDate\": \"2004/01/09\",\n" +
                                "  \"endDate\": \"2004/01/" +
                                "12\",\n" +
                                "  \"luxury\": false,\n" +
                                "  \"wasHold\": false,\n" +
                                "  \"eventType\": \"PARTY\",\n" +
                                "  " +
                                "\"organizerToken\": null,\n" +
                                "  \"locationToken\": null,\n" +
                                "  \"tagNames\": [\"epic\", \"free\"]\n" +
                                "}")
                ).andExpect(status().isCreated())
                .andReturn();

        String eventToken = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(eventToken).isNotBlank();

        var createResponse1 = mockMvc.perform(
                        post("/api/v1/time-slots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"from\": \"2022/01/01 20:00:00\",\n" +
                                        "  \"to\": \"2022/01/01 20:30:00\",\n" +
                                        "  \"name\": \"Opening\",\n" +
                                        "  \"description\": \"123\",\n" +
                                        "  " +
                                        "\"contactToken\": null,\n" +
                                        "  \"eventToken\": \"%s\"\n".formatted(eventToken) +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andReturn();
        String token1 = JsonPath.read(createResponse1.getResponse().getContentAsString(), "$._token");

        var createResponse2 = mockMvc.perform(
                        post("/api/v1/time-slots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"from\": \"2022/01/02 00:00:00\",\n" +
                                        "  \"to\": \"2022/01/01 00:30:00\",\n" +
                                        "  \"name\": \"Midnight-Special\",\n" +
                                        "  \"description\": null,\n" +
                                        "  " +
                                        "\"contactToken\": null,\n" +
                                        "  \"eventToken\": null\n" +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andReturn();
        String token2 = JsonPath.read(createResponse2.getResponse().getContentAsString(), "$._token");

        mockMvc.perform(get("/api/v1/time-slots"))
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(delete("/api/v1/time-slots/%s".formatted(token2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/time-slots"))
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/events/%s/time-slots".formatted(eventToken)))
                .andExpect(jsonPath("$.length()").value(1));

    }
}
