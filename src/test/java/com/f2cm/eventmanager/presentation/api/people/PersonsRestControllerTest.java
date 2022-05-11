package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.commands.CreatePersonCommand;
import com.f2cm.eventmanager.service.fixtures.PersonFixtures;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
class PersonsRestControllerTest {

    @MockBean
    private PersonService personService;

    private ObjectMapper objectMapper;

    public MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new PersonsRestController(personService)).build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Test
    void ensureGetPersonsReturnsOkResponseWhenDataIsAvailable() throws Exception {
        Person person = PersonFixtures.getPersonFixture1();
        when(personService.getPersons()).thenReturn(List.of(person));
        mockMvc.perform(get(PersonsRestController.BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]._token").value(person.getToken()))
                .andExpect(jsonPath("$[0]._createdAt").value(person.getCreatedAt()))
                .andExpect(jsonPath("$[0].firstName").value(person.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(person.getLastName()))
                .andExpect(jsonPath("$[0].titleFront").value(person.getTitleFront()))
                .andExpect(jsonPath("$[0].titleBack").value(person.getTitleBack()))
                .andExpect(jsonPath("$[0].birthday").value(person.getBirthday()))
                .andExpect(jsonPath("$[0].mainJob").value(person.getMainJob()))
                .andExpect(jsonPath("$[0].gender").value(person.getGender()))
                .andExpect(jsonPath("$[0].address.streetNumber").value(person.getAddress().getStreetNumber()))
                .andExpect(jsonPath("$[0].address.zipCode").value(person.getAddress().getZipCode()))
                .andExpect(jsonPath("$[0].address.city").value(person.getAddress().getCity()))
                .andExpect(jsonPath("$[0].address.country").value(person.getAddress().getCountry()));
    }

    @Test
    void ensureGetePersonReturnsNotFoundResponseWhenDataIsNotAvailable() throws Exception {
        when(personService.getOptionalPerson(any())).thenReturn(Optional.empty());
        mockMvc.perform(get(PersonsRestController.BASE_URL + "/empty").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void ensureGetPersonReturnsOkResponseWhenDataIsAvailable() throws Exception {
        Person person = PersonFixtures.getPersonFixture1();
        when(personService.getOptionalPerson(person.getToken())).thenReturn(Optional.of(person));
        mockMvc.perform(get(PersonsRestController.BASE_URL + "/" + person.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstName").value(person.getFirstName()))
                .andExpect(jsonPath("$._token").value(person.getToken()));
    }

    @Test
    void ensureCreateEventRoleWorksWithGivenData() throws Exception {
        Person person = PersonFixtures.getPersonFixture1();
        CreatePersonCommand createPersonCommand = PersonFixtures.getCreatePersonCommandFixture();
        when(personService.createPerson(any())).thenReturn(person);
        mockMvc.perform(post(PersonsRestController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(person.getToken()))
                .andExpect(jsonPath("$.firstName").value(person.getFirstName()));
    }

    @Test
    void ensureReplacePersonReturnsOkResponseWhenDataIsGiven() throws Exception {
        Person person = PersonFixtures.getPersonFixture1();
        CreatePersonCommand createPersonCommand = PersonFixtures.getCreatePersonCommandFixture();
        when(personService.replacePerson(any(), any())).thenReturn(person);
        mockMvc.perform(put(PersonsRestController.BASE_URL + "/" + person.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonCommand)))
            .andDo(print()).andExpect(status().isOk());
    }

}
