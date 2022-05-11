package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.service.dtos.objs.people.UpdateContactTypeSocialNetworkCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.types.people.ContactTypeService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class ContactTypeRestControllerTest {

    @MockBean
    private ContactTypeService contactTypeService;

    private ObjectMapper objectMapper;

    public MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ContactTypeRestController(contactTypeService)).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void ensureGetContactTypesReturnsOkResponseWhenDataIsAvailable() throws Exception {
        ContactType contactType = ContactType.builder().means("email").socialNetwork(false).build();
        when(contactTypeService.getContactTypes()).thenReturn(List.of(contactType));
        mockMvc.perform(get(ContactTypeRestController.BASE_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].means").value(contactType.getMeans()))
                .andExpect(jsonPath("$[0].socialNetwork").value(contactType.isSocialNetwork()));
    }

    @Test
    void ensureGetContactTypeReturnsNotFoundResponseWhenDataIsNotAvailable() throws Exception {
        when(contactTypeService.getContactTypeOptional(any())).thenReturn(Optional.empty());
        mockMvc.perform(get(ContactTypeRestController.BASE_URL + "/empty").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void ensureGetContactTypeReturnsOkResponseWhenDataIsAvailable() throws Exception {
        ContactType contactType = ContactType.builder().means("email").socialNetwork(false).build();
        when(contactTypeService.getContactTypeOptional(contactType.getMeans())).thenReturn(Optional.of(contactType));
        mockMvc.perform(get(ContactTypeRestController.BASE_URL + "/" + contactType.getMeans()).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.means").value(contactType.getMeans()))
                .andExpect(jsonPath("$.socialNetwork").value(contactType.isSocialNetwork()));
    }

    @Test
    void ensurePatchContactTypeSocialNetworkStatusReturnsOkResponse() throws Exception {
        ContactType contactType = ContactType.builder().means("email").socialNetwork(false).build();
        UpdateContactTypeSocialNetworkCommand update = new UpdateContactTypeSocialNetworkCommand(!contactType.isSocialNetwork());
        mockMvc.perform(patch(ContactTypeRestController.BASE_URL + "/" + contactType.getMeans())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(contactTypeService).partiallyUpdateContactType(any(), any());
    }

    @Test
    void ensurePatchContactTypeSocialNetworkStatusReturnsNotFoundResponseWhenMeansDoesNotExist() throws Exception {
        when(contactTypeService.partiallyUpdateContactType(any(), any())).thenThrow(NotFoundException.cannotFindEntityByToken(ContactType.class, "token"));
        UpdateContactTypeSocialNetworkCommand update = new UpdateContactTypeSocialNetworkCommand(false);
        mockMvc.perform(patch(ContactTypeRestController.BASE_URL + "/notfound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
