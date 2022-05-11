package com.f2cm.eventmanager.presentation.api.places;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@EnableAutoConfiguration
class LocationRestControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(delete("/api/v1/locations"));
    }

    @Test
    public void ensureThatCreatingAndDeletingLocationsWorks() throws Exception {
        var req1 = mockMvc.perform(post("/api/v1/locations")
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

        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(token1).isNotBlank();

        var req2 = mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"locationType\": \"OUTDOOR_LOCATION\",\n" +
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
                                "  \"weatherSave\": true,\n" +
                                "  \"hasPool\": true,\n" +
                                "  \"hasCampFire\": false,\n" +
                                "  \"hasOutdoorStage\": true,\n" +
                                "  \"numOfPlaces\": 2\n" +
                                "}")
                )
                .andExpect(status().isCreated())
                .andReturn();

        String token2 = JsonPath.read(req2.getResponse().getContentAsString(), "$._token");
        assertThat(token2).isNotBlank();

        mockMvc.perform(get("/api/v1/locations/%s".formatted(token2)))
                .andExpect(status().isOk());

        var req3 = mockMvc.perform(get("/api/v1/locations/"))
                .andExpect(status().isOk())
                .andReturn();

        int length1 = JsonPath.read(req3.getResponse().getContentAsString(), "$.length()");
        assertThat(length1).isEqualTo(2);

        mockMvc.perform(delete("/api/v1/locations/%s".formatted(token1)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/locations/%s".formatted(token1)))
                .andExpect(status().is4xxClientError())
                .andReturn();

        var req4 = mockMvc.perform(get("/api/v1/locations/"))
                .andExpect(status().isOk())
                .andReturn();

        int length2 = JsonPath.read(req4.getResponse().getContentAsString(), "$.length()");
        assertThat(length2).isEqualTo(1);

    }

    @Test
    public void ensureThatUpdatingLocationWorks() throws Exception {
        var req1 = mockMvc.perform(post("/api/v1/locations")
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
        String token1 = JsonPath.read(req1.getResponse().getContentAsString(), "$._token");
        assertThat(token1).isNotBlank();

        mockMvc.perform(put("/api/v1/locations/%s".formatted(token1))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"locationType\": \"INDOOR_LOCATION\",\n" +
                        "  \"squareMeters\": 50,\n" +
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
        ).andExpect(status().isOk());

        var req2 = mockMvc.perform(get("/api/v1/locations/%s".formatted(token1)))
                .andExpect(status().isOk())
                .andReturn();

        int squareMeters = JsonPath.read(req2.getResponse().getContentAsString(), "$.squareMeters");
        assertThat(squareMeters).isEqualTo(50);

    }

}
