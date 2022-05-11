package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventParticipantCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateEventParticipantCommand;
import com.f2cm.eventmanager.service.fixtures.*;
import com.f2cm.eventmanager.service.types.people.EventParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
class EventParticipationRestControllerTest {

    public MockMvc mockMvc;

    @MockBean
    private EventParticipantService eventParticipantService;

    private ObjectMapper objectMapper;
    private static final String PERSON_TOKEN_PARAM_KEY = "person_token";
    private static final String EVENT_TOKEN_PARAM_KEY = "event_token";

    private Person person;
    private Location location;
    private String eventParticipantToken;
    private String slugName;
    private EventRole eventRole;
    private Event event;
    private Tag tag;
    private EventParticipant eventParticipant;


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EventParticipationRestController(eventParticipantService)).build();
        this.objectMapper = new ObjectMapper();

        person = PersonFixtures.getPersonFixture1();
        location = LocationFixtures.getOutdoorLocation1(person);
        tag = TagFixtures.getTag1();
        event = EventFixtures.getEvent1(location, Set.of(tag));
        eventParticipantToken = "token-evp";
        slugName = "Boss";
        eventRole = EventRoleFixture.getEventRole1();
        eventParticipant = EventParticipant.builder()
                .token(eventParticipantToken)
                .eventRole(eventRole)
                .createdAt(LocalDateTime.now())
                .event(event)
                .person(person)
                .build();
    }

    @Test
    void ensureGetEventParticipantsForEventAndPersonsReturnsBadRequestWhenBothTokensAreGiven() throws Exception {
        mockMvc.perform(get(EventParticipationRestController.BASE_URL)
                        .param(PERSON_TOKEN_PARAM_KEY, "person-token")
                        .param(EVENT_TOKEN_PARAM_KEY, "event-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ensureGetEventParticipantForEventOrPersonReturnsOkResponseWhenNoTokenIsGiven() throws Exception {
        when(eventParticipantService.getEventParticipants()).thenReturn(List.of(eventParticipant));
        mockMvc.perform(get(EventParticipationRestController.BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]._token").value(eventParticipant.getToken()));
    }

    @Test
    void ensureGetEventParticipantsForEventReturnsOkResponseWhenDataIsAvailable() throws Exception {
        when(eventParticipantService.getEventParticipantsForEvent(event.getToken())).thenReturn(List.of(eventParticipant));
        mockMvc.perform(get(EventParticipationRestController.BASE_URL)
                        .param(EVENT_TOKEN_PARAM_KEY, event.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]._token").value(eventParticipant.getToken()));
    }

    @Test
    void ensureGetEventParticipantsForPersonReturnsOkResponseWhenDataIsAvailable() throws Exception {
        when(eventParticipantService.getEventParticipantsForPerson(person.getToken())).thenReturn(List.of(eventParticipant));
        mockMvc.perform(get(EventParticipationRestController.BASE_URL)
                        .param(PERSON_TOKEN_PARAM_KEY, person.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]._token").value(eventParticipant.getToken()));
    }

    @Test
    void ensureGetEventParticipantReturnsOkResponseWhenDataIsGiven() throws Exception {
        when(eventParticipantService.getEventParticipantOptional(eventParticipantToken)).thenReturn(Optional.ofNullable(eventParticipant));
        mockMvc.perform(get(EventParticipationRestController.BASE_URL + "/" + eventParticipantToken).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(eventParticipant.getToken()))
                .andExpect(jsonPath("$.event._token").value(event.getToken()))
                .andExpect(jsonPath("$.person._token").value(person.getToken()))
                .andExpect(jsonPath("$.eventRole._slug").value(eventRole.getSlug()));
    }

    @Test
    void ensureCreateEventParticipantReturnsCreatedResponseWhenDataIsGiven() throws Exception {
        CreateEventParticipantCommand createEventParticipantCommand = new CreateEventParticipantCommand(event.getToken(), person.getToken(), slugName, false, false);
        when(eventParticipantService.registerEventParticipantForEvent(any())).thenReturn(eventParticipant);
        mockMvc.perform(post(EventParticipationRestController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventParticipantCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(eventParticipant.getToken()));
    }

    @Test
    void ensureUpdateEventParticipantUpdatesEventParticipant() throws Exception {
        String newRoleName = "new event role";
        UpdateEventParticipantCommand updateEventParticipantCommand = new UpdateEventParticipantCommand(false, false, newRoleName);
        eventParticipant.setEventRole(EventRole.builder().name(newRoleName).slug("new-event-role").build());
        when(eventParticipantService.updateEventParticipant(any(), any())).thenReturn(eventParticipant);
        mockMvc.perform(put(EventParticipationRestController.BASE_URL + "/" + eventParticipantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEventParticipantCommand)))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
