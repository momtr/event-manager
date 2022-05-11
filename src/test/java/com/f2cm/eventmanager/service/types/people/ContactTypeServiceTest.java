package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.ContactRepository;
import com.f2cm.eventmanager.persistence.types.people.ContactTypeRepository;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactTypeCommand;
import com.f2cm.eventmanager.service.exceptions.CannotDeleteEntityException;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactTypeServiceTest {

    @Mock
    private ContactTypeRepository contactTypeRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private TemporalValueFactory temporalValueFactory;

    private ContactTypeService contactTypeService;

    @BeforeEach
    void setup() {
        assertThat(contactTypeRepository).isNotNull();
        assertThat(contactRepository).isNotNull();
        assertThat(temporalValueFactory).isNotNull();
        contactTypeService = new ContactTypeService(contactTypeRepository, contactRepository, temporalValueFactory);
    }

    @Test
    void ensureCreateContactTypeWorksProperly() {
        CreateContactTypeCommand createContactTypeCommand = new CreateContactTypeCommand("email", false);
        LocalDateTime ts = LocalDateTime.now();
        when(temporalValueFactory.now()).thenReturn(ts);
        ContactType contactType = contactTypeService.createContactType(createContactTypeCommand);
        assertThat(contactType).isNotNull();
        assertThat(contactType.getCreatedAt()).isEqualTo(ts);
        verify(contactTypeRepository).save(any());
    }

    @Test
    void ensureGetContactTypeWorksProperly() {
        String means = "email";
        ContactType contactType = ContactType.builder()
                .means(means)
                .build();
        when(contactTypeRepository.findByMeans(means)).thenReturn(Optional.of(contactType));
        ContactType foundContactType = contactTypeService.getContactType(means);
        assertThat(foundContactType).isEqualTo(contactType);
        verify(contactTypeRepository).findByMeans(means);
    }

    @Test
    void ensureGetContactTypeThrowsExceptionWithUnknownMeans() {
        String means = "email";
        when(contactTypeRepository.findByMeans(means)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> contactTypeService.getContactType(means));
    }

    @Test
    void ensureDeleteContactTypeDoesNotDeleteContactTypeWhenTheyAreReferenced() {
        String means = "email";
        ContactType contactType = ContactType.builder().means(means).build();
        when(contactTypeRepository.findByMeans(means)).thenReturn(Optional.of(contactType));
        when(contactRepository.countAllByContactType(contactType)).thenReturn(1l);
        assertThrows(CannotDeleteEntityException.class, () -> contactTypeService.deleteContactType(means));
        verifyNoMoreInteractions(contactTypeRepository);
    }

    @Test
    void ensureDeleteContactTypeDeletesContactTypeWhenTheyAreNotReferenced() {
        String means = "email";
        ContactType contactType = ContactType.builder().means(means).build();
        when(contactTypeRepository.findByMeans(means)).thenReturn(Optional.of(contactType));
        when(contactRepository.countAllByContactType(contactType)).thenReturn(0l);
        contactTypeService.deleteContactType(means);
        verify(contactTypeRepository).deleteByMeans(any());
    }

}
