package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateAddressCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
class ContactRestControllerTest {

    @MockBean
    private PersonService personService;

    private ObjectMapper objectMapper;

    public MockMvc mockMvc;

    private final String PERSON_TOKEN_PARAMETER_KEY = "person_token";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ContactRestController(personService)).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void ensureGetContactForPersonReturnsOkResponseWhenDataIsGiven() throws Exception {
        ContactType contactType = ContactType.builder().means("email").build();
        Contact contact = Contact.builder().token("token").value("mitterdorfer.moritz@gmail.com").contactType(contactType).build();
        when(personService.getContactsForPerson(anyString())).thenReturn(Set.of(contact));
        mockMvc.perform(get(ContactRestController.BASE_URL)
                        .param(PERSON_TOKEN_PARAMETER_KEY, "person_test_token")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]._token").value(contact.getToken()))
                .andExpect(jsonPath("$[0].type").value(contact.getContactType().getMeans()))
                .andExpect(jsonPath("$[0].value").value(contact.getValue()));
    }

    @Test
    void ensureGetContactForPersonReturnsNotFoundWhenPersonWithGivenTokenDoesNotExist() throws Exception {
        when(personService.getContactsForPerson(anyString())).thenThrow(NotFoundException.cannotFindEntityByToken(Person.class, "test_token"));
        mockMvc.perform(get(ContactRestController.BASE_URL)
                .param(PERSON_TOKEN_PARAMETER_KEY, "person_test_token")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    void ensureGetContactReturnsOkResponseWhenDataIsAvailable() throws Exception {
        ContactType contactType = ContactType.builder().means("email").build();
        Contact contact = Contact.builder().token("token").value("mitterdorfer.moritz@gmail.com").contactType(contactType).build();
        when(personService.findContactByToken(anyString())).thenReturn(contact);
        mockMvc.perform(get(ContactRestController.BASE_URL + "/token")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(contact.getToken()))
                .andExpect(jsonPath("$.type").value(contact.getContactType().getMeans()))
                .andExpect(jsonPath("$.value").value(contact.getValue()));
    }

    @Test
    void ensureSetPrimaryContactForPersonCreatesNewPrimaryContact() throws Exception {
        String personToken = "person-token";
        String email = "email";
        String address = "mitterdorfer.moritz@gmail.com";
        ContactType contactType = ContactType.builder().means(email).build();
        Contact contact = Contact.builder().token("contact-token").value(address).contactType(contactType).build();
        CreateContactCommand createContactCommand = new CreateContactCommand(email, true, address, true);
        when(personService.attachPrimaryContactToPerson(anyString(), any())).thenReturn(contact);
        mockMvc.perform(post(ContactRestController.BASE_URL + "/primary")
                        .param(PERSON_TOKEN_PARAMETER_KEY, personToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createContactCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(contact.getToken()))
                .andExpect(jsonPath("$.type").value(contact.getContactType().getMeans()))
                .andExpect(jsonPath("$.value").value(contact.getValue()));
    }

    @Test
    void ensureSetPrimaryContactReturnsBadRequestWhenReqBodyNotComplete() throws Exception {
        CreateContactCommand createContactCommand = CreateContactCommand.builder().address("address").build();
        Contact contact = Contact.builder().token("contact-token").build();
        when(personService.attachPrimaryContactToPerson(anyString(), any())).thenReturn(contact);
        mockMvc.perform(post(ContactRestController.BASE_URL + "/primary")
                        .param(PERSON_TOKEN_PARAMETER_KEY, "person_test_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createContactCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void ensureCreateContactForPersonReturnsCreatedResponseWhenDataIsGiven() throws Exception {
        String personToken = "person-token";
        String email = "email";
        String address = "mitterdorfer.moritz@gmail.com";
        ContactType contactType = ContactType.builder().means(email).build();
        Contact contact = Contact.builder().token("contact-token").value(address).contactType(contactType).build();
        CreateContactCommand createContactCommand = new CreateContactCommand(email, true, address, true);
        when(personService.attachContactToPerson(anyString(), any(), anyBoolean())).thenReturn(contact);
        mockMvc.perform(post(ContactRestController.BASE_URL)
                        .param(PERSON_TOKEN_PARAMETER_KEY, personToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createContactCommand)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$._token").value(contact.getToken()))
                .andExpect(jsonPath("$.type").value(contact.getContactType().getMeans()))
                .andExpect(jsonPath("$.value").value(contact.getValue()));
    }

    @Test
    void ensurePatchAddressForContactReturnsOkResponseWhenDataIsGiven() throws Exception {
        String newAddress = "new_address";
        String token = "mytoken";
        UpdateAddressCommand updateAddressCommand = new UpdateAddressCommand(newAddress);
        when(personService.editContactAddressForPerson(anyString(), anyString())).thenReturn(Contact.builder().value(newAddress).build());
        mockMvc.perform(patch(ContactRestController.BASE_URL + "/" + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAddressCommand)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
