package com.f2cm.eventmanager.presentation.api.events;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class EventRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private String locationToken;

    @BeforeEach
    void setUp() throws Exception {
        var createLocationResult = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"locationType\": \"INDOOR_LOCATION\",\n" +
                                "  \"squareMeters\": 100,\n" +
                                "  \"numOfToilets\": 2,\n" +
                                "  \"numOfBars\": 2,\n" +
                                "  \"maxDezibel\": 50,\n" +
                                "  \"numOfParkingPlaces\": 10,\n" +
                                "  \"opensAt\": \"15:40\",\n" +
                                "  \"endsAt\": \"23:00\",\n" +
                                "  \"name\": \"HTL Spengergasse\",\n" +
                                "  \"streetNumber\": \"Spengergasse 1\",\n" +
                                "  \"zipCode\": \"1050\",\n" +
                                "  \"city\": \"Wien\",\n" +
                                "  \"country\": \"Österreich\",\n" +
                                "  \"numOfFloors\": 3,\n" +
                                "  \"numOfEntrances\": 1,\n" +
                                "  \"numOfReceptions\": 1,\n" +
                                "  \"numOfRomms\": 15\n" +
                                "}")
                ).andExpect(status().isCreated())
                .andReturn();

        locationToken = JsonPath.read(createLocationResult.getResponse().getContentAsString(), "$._token");
    }

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(delete("/api/v1/events"));
        mockMvc.perform(delete("/api/v1/locations"));
        mockMvc.perform(delete("/api/v1/tags"));
    }

    @Test
    public void ensureThatCreatingAndDeletingEventsWorks() throws Exception {
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

        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(token1).isNotBlank();

        var req2 = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"Second Switch XXL\",\n" +
                                "  \"startDate\": \"2004/01/09\",\n" +
                                "  \"endDate\": \"2004/01/12\",\n" +
                                "  \"luxury\": false,\n" +
                                "  \"wasHold\": false,\n" +
                                "  \"eventType\": \"PARTY\",\n" +
                                "  " +
                                "\"organizerToken\": null,\n" +
                                "  \"locationToken\": \"%s\",\n".formatted(locationToken) +
                                "  \"tagNames\": [\"not so epic\"]\n" +
                                "}")
                )
                .andExpect(status().isCreated())
                .andReturn();

        String token2 = JsonPath.read(req2.getResponse().getContentAsString(), "$._token");
        assertThat(token2).isNotBlank();

        mockMvc.perform(get("/api/v1/events/%s".formatted(token2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/events/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        mockMvc.perform(delete("/api/v1/events/%s".formatted(token1)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/events/%s".formatted(token1)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        mockMvc.perform(get("/api/v1/events/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

    }

    @Test
    public void ensureThatUpdatingEventsWorks() throws Exception {
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

        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat((Object) JsonPath.read(req1.getResponse().getContentAsString(), "$.location")).isNull();


        mockMvc.perform(put("/api/v1/events/%s".formatted(token1))
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
                                "  \"locationToken\": \"%s\",\n".formatted(locationToken) +
                                "  \"tagNames\": [\"epic\", \"free\"]\n" +
                                "}")
                ).andExpect(status().isOk())
                .andReturn();

        var req2 = mockMvc.perform(get("/api/v1/events/%s".formatted(token1)))
                .andReturn();
        assertThat((Object) JsonPath.read(req2.getResponse().getContentAsString(), "$.location")).isNotNull();
    }

    @Test
    public void ensureThatFilteringEventsWorks() throws Exception {
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

        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(token1).isNotBlank();

        var req2 = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"Second Switch XXL\",\n" +
                                "  \"startDate\": \"2004/01/09\",\n" +
                                "  \"endDate\": \"2004/01/12\",\n" +
                                "  \"luxury\": false,\n" +
                                "  \"wasHold\": false,\n" +
                                "  \"eventType\": \"PARTY\",\n" +
                                "  " +
                                "\"organizerToken\": null,\n" +
                                "  \"locationToken\": \"%s\",\n".formatted(locationToken) +
                                "  \"tagNames\": [\"not so epic\"]\n" +
                                "}")
                )
                .andExpect(status().isCreated())
                .andReturn();

        String token2 = JsonPath.read(req2.getResponse().getContentAsString(), "$._token");
        assertThat(token2).isNotBlank();

        mockMvc.perform(get("/api/v1/events").param("tags", "epic"))
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        mockMvc.perform(get("/api/v1/events").param("tags", "random"))
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        mockMvc.perform(get("/api/v1/events").param("tags", "epic", "not so epic", "free"))
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();
    }

    @Test
    public void ensureThatAddingAndRemovingTagsWorks() throws Exception {
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

        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(token1).isNotBlank();


        mockMvc.perform(get("/api/v1/events").param("tags", "epic"))
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        mockMvc.perform(get("/api/v1/events").param("tags", "random"))
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        mockMvc.perform(post("/api/v1/events/%s/tags/random".formatted(token1)));
        mockMvc.perform(post("/api/v1/events/%s/tags/free".formatted(token1)));
        mockMvc.perform(get("/api/v1/events").param("tags", "free"))
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

         mockMvc.perform(get("/api/v1/events/%s/tags".formatted(token1)))
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn();

        mockMvc.perform(delete("/api/v1/events/%s/tags/free".formatted(token1)));
        mockMvc.perform(get("/api/v1/events").param("tags","free"))
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        mockMvc.perform(get("/api/v1/events/%s/tags".formatted(token1)))
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(jsonPath("$.length()").isNumber())
                .andReturn();

    }
}
