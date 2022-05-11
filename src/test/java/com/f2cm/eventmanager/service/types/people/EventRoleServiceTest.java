package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.EventParticipantRepository;
import com.f2cm.eventmanager.persistence.types.people.EventRoleRepository;
import com.f2cm.eventmanager.service.SlugService;
import com.f2cm.eventmanager.service.exceptions.CannotDeleteEntityException;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.fixtures.EventRoleFixture;
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
class EventRoleServiceTest {

    @Mock
    private TemporalValueFactory temporalValueFactory;

    @Mock
    private EventRoleRepository eventRoleRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private SlugService slugService;

    private EventRoleService eventRoleService;

    @BeforeEach
    void setup() {
        assertThat(temporalValueFactory).isNotNull();
        assertThat(eventRoleRepository).isNotNull();
        assertThat(eventParticipantRepository).isNotNull();
        assertThat(slugService).isNotNull();
        eventRoleService = new EventRoleService(eventRoleRepository, eventParticipantRepository, temporalValueFactory, slugService);
    }

    @Test
    void ensureCreatingEventRoleWorksProperly() {
        String name = "Head Manager";
        String slug = "event-role";
        LocalDateTime ts = LocalDateTime.now();
        when(slugService.generateSlugAsToken(any())).thenReturn(slug);
        when(temporalValueFactory.now()).thenReturn(ts);
        when(eventRoleRepository.findEventRoleByName(any())).thenReturn(Optional.empty());
        EventRole eventRole = eventRoleService.createEventRole(name);
        assertThat(eventRole).isNotNull();
        assertThat(eventRole.getSlug()).isEqualTo(slug);
        assertThat(eventRole.getName()).isEqualTo(name);
        assertThat(eventRole.getCreatedAt()).isEqualTo(ts);
    }

    @Test
    void ensureCreatingEventRoleDoesNotDuplicateRole() {
        String name = "Head Manager";
        String slug = "event-role";
        EventRole existingEventRole = EventRole.builder().slug(slug).name(name).build();
        when(eventRoleRepository.findEventRoleByName(name)).thenReturn(Optional.of(existingEventRole));
        EventRole eventRole = eventRoleService.createEventRole(name);
        assertThat(eventRole).isNotNull();
        assertThat(eventRole).isEqualTo(existingEventRole);
    }

    @Test
    void ensureGetEventRoleWorksProperly() {
        String name = "Head Manager";
        String slug = "event-role";
        EventRole existingEventRole = EventRole.builder().slug(slug).name(name).build();
        when(eventRoleRepository.findBySlug(slug)).thenReturn(Optional.of(existingEventRole));
        EventRole eventRole = eventRoleService.getEventRole(slug);
        assertThat(eventRole).isNotNull();
        assertThat(eventRole).isEqualTo(existingEventRole);
    }

    @Test
    void ensureGetEventRoleThatDoesNotExistThrrowsException() {
        assertThrows(NotFoundException.class, () -> eventRoleService.getEventRole("unknown-slug"));
    }

    @Test
    void ensureDeleteEventRoleDeletesEventRoleWhenThereAreNoDependencies() {
        String slug = "slug";
        EventRole eventRole = EventRoleFixture.getEventRole1();
        when(eventRoleRepository.findBySlug(slug)).thenReturn(Optional.ofNullable(eventRole));
        when(eventParticipantRepository.countAllByEventRole(eventRole)).thenReturn(0l);
        eventRoleService.deleteEventRole(slug);
        verify(eventRoleRepository).deleteBySlug(slug);
    }

    @Test
    void ensureDeleteEventRoleDoesNotDeleteEventRoleWhenThereAreDependencies() {
        String slug = "slug";
        EventRole eventRole = EventRoleFixture.getEventRole1();
        when(eventRoleRepository.findBySlug(slug)).thenReturn(Optional.ofNullable(eventRole));
        when(eventParticipantRepository.countAllByEventRole(eventRole)).thenReturn(1l);
        assertThrows(CannotDeleteEntityException.class, () -> eventRoleService.deleteEventRole(slug));
        verifyNoMoreInteractions(eventRoleRepository);
    }

}
