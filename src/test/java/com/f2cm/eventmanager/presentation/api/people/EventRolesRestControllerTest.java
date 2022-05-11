package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventRoleCommand;
import com.f2cm.eventmanager.service.fixtures.EventRoleFixture;
import com.f2cm.eventmanager.service.types.people.EventRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class EventRolesRestControllerTest {

    @MockBean
    private EventRoleService eventRoleService;

    private ObjectMapper objectMapper;

    public MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EventRolesRestController(eventRoleService)).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void ensureGetEventRolesReturnsOkResponseWhenDataIsAvailable() throws Exception {
        EventRole eventRole = EventRoleFixture.getEventRole1();
        when(eventRoleService.getEventRoles()).thenReturn(List.of(eventRole));
        mockMvc.perform(get(EventRolesRestController.BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].name").value(eventRole.getName()))
                .andExpect(jsonPath("$[0]._slug").value(eventRole.getSlug()));
    }

    @Test
    void ensureGetEventRoleReturnsNotFoundResponseWhenDataIsNotAvailable() throws Exception {
        when(eventRoleService.getEventRoleOptional(any())).thenReturn(Optional.empty());
        mockMvc.perform(get(EventRolesRestController.BASE_URL + "/empty").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void ensureGetEventRoleReturnsOkResponseWhenDataIsAvailable() throws Exception {
        EventRole eventRole = EventRoleFixture.getEventRole1();
        when(eventRoleService.getEventRoleOptional(eventRole.getSlug())).thenReturn(Optional.of(eventRole));
        mockMvc.perform(get(EventRolesRestController.BASE_URL + "/" + eventRole.getSlug()).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.name").value(eventRole.getName()))
                .andExpect(jsonPath("$._slug").value(eventRole.getSlug()));
    }

    @Test
    void ensureCreateEventRoleWorksWithGivenData() throws Exception {
        EventRole eventRole = EventRoleFixture.getEventRole2();
        CreateEventRoleCommand createEventRoleCommand = new CreateEventRoleCommand(eventRole.getName());
        when(eventRoleService.createEventRole(createEventRoleCommand.getName())).thenReturn(eventRole);
        mockMvc.perform(post(EventRolesRestController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEventRoleCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.name").value(eventRole.getName()))
                .andExpect(jsonPath("$._slug").value(eventRole.getSlug()));
    }

}
