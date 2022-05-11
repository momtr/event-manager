package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.EventParticipantRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventParticipantCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateEventParticipantCommand;
import com.f2cm.eventmanager.service.fixtures.*;
import com.f2cm.eventmanager.service.types.events.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipantServiceTest {

    @Mock
    private EventRoleService eventRoleService;

    @Mock
    private PersonService personService;

    @Mock
    private EventService eventService;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private TemporalValueFactory temporalValueFactory;

    private EventParticipantService eventParticipantService;

    @BeforeEach
    void setup() {
        assertThat(eventRoleService).isNotNull();
        assertThat(personService).isNotNull();
        assertThat(eventService).isNotNull();
        assertThat(eventService).isNotNull();
        assertThat(tokenService).isNotNull();
        assertThat(temporalValueFactory).isNotNull();
        eventParticipantService = new EventParticipantService(eventRoleService, personService, eventService, eventParticipantRepository, tokenService, temporalValueFactory);
    }

    @Test
    void ensureRegisterEventParticipationForEventWorksProperly() {
        Event e1 = EventFixtures.getEvent1(LocationFixtures.getOutdoorLocation1(PersonFixtures.getPersonFixture1()), Set.of(TagFixtures.getTag1()));
        Person p1 = PersonFixtures.getPersonFixture2();
        EventRole er1 = EventRoleFixture.getEventRole1();
        LocalDateTime ts = LocalDateTime.now();
        String token = "TOKEN-xyz";
        when(eventService.getEvent(e1.getToken())).thenReturn(e1);
        when(personService.getPerson(p1.getToken())).thenReturn(p1);
        when(eventRoleService.getEventRoleOptional(er1.getSlug())).thenReturn(Optional.of(er1));
        when(temporalValueFactory.now()).thenReturn(ts);
        when(tokenService.createNanoIdWithType(any())).thenReturn(token);
        when(eventParticipantRepository.findByPersonAndEventAndEventRole(any(), any(), any())).thenReturn(Optional.empty());
        CreateEventParticipantCommand createEventParticipantCommand = new CreateEventParticipantCommand(e1.getToken(), p1.getToken(), er1.getSlug(), true, true);
        EventParticipant eventParticipant = eventParticipantService.registerEventParticipantForEvent(createEventParticipantCommand);
        assertThat(eventParticipant).isNotNull();
        assertThat(eventParticipant.getEvent()).isEqualTo(e1);
        assertThat(eventParticipant.getPerson()).isEqualTo(p1);
        assertThat(eventParticipant.getEventRole()).isEqualTo(er1);
        assertThat(eventParticipant.isPaid()).isTrue();
        assertThat(eventParticipant.isInternal()).isTrue();
        assertThat(eventParticipant.getToken()).isEqualTo(token);
        assertThat(eventParticipant.getCreatedAt()).isEqualTo(ts);
        verify(eventParticipantRepository).findByPersonAndEventAndEventRole(p1, e1, er1);
        verify(eventParticipantRepository).save(any());
        verifyNoMoreInteractions(eventParticipantRepository);
    }

    @Test
    void ensureRegisterEventParticipationReturnsEventParticipationIfAlreadyExists() {
        Event e1 = EventFixtures.getEvent1(LocationFixtures.getOutdoorLocation1(PersonFixtures.getPersonFixture1()), Set.of(TagFixtures.getTag1()));
        Person p1 = PersonFixtures.getPersonFixture2();
        EventRole er1 = EventRoleFixture.getEventRole1();
        LocalDateTime ts = LocalDateTime.now();
        String token = "TOKEN-xyz";
        EventParticipant eventParticipant = EventParticipant.builder()
                .event(e1)
                .person(p1)
                .eventRole(er1)
                .token(token)
                .createdAt(ts)
                .build();
        when(eventService.getEvent(e1.getToken())).thenReturn(e1);
        when(personService.getPerson(p1.getToken())).thenReturn(p1);
        when(eventRoleService.getEventRoleOptional(er1.getSlug())).thenReturn(Optional.of(er1));
        when(eventParticipantRepository.findByPersonAndEventAndEventRole(any(), any(), any())).thenReturn(Optional.of(eventParticipant));
        CreateEventParticipantCommand createEventParticipantCommand = new CreateEventParticipantCommand(e1.getToken(), p1.getToken(), er1.getSlug(), true, true);
        EventParticipant registeredParticipant = eventParticipantService.registerEventParticipantForEvent(createEventParticipantCommand);
        assertThat(registeredParticipant).isEqualTo(eventParticipant);
    }

    @Test
    void ensureUpdateEventParticipantWorksProperlyWithUnchangedEventRole() {
        Event e1 = EventFixtures.getEvent1(LocationFixtures.getOutdoorLocation1(PersonFixtures.getPersonFixture1()), Set.of(TagFixtures.getTag1()));
        String token = "token-764376236";
        EventRole eventRole = EventRoleFixture.getEventRole1();
        EventParticipant eventParticipant = EventParticipant.builder()
                .event(e1)
                .person(PersonFixtures.getPersonFixture2())
                .eventRole(eventRole)
                .token(token)
                .createdAt(LocalDateTime.now())
                .paid(true)
                .internal(true)
                .build();
        when(eventParticipantRepository.findByToken(any())).thenReturn(Optional.of(eventParticipant));
        when(eventRoleService.getEventRoleOptional(any())).thenReturn(Optional.empty());
        when(eventRoleService.createEventRole(any())).thenReturn(eventRole);
        assertThat(eventParticipant.isInternal()).isTrue();
        assertThat(eventParticipant.isPaid()).isTrue();
        UpdateEventParticipantCommand updateEventParticipantCommand = new UpdateEventParticipantCommand(false, false, "event-role-name");
        eventParticipantService.updateEventParticipant(token, updateEventParticipantCommand);
        assertThat(eventParticipant).isNotNull();
        assertThat(eventParticipant.isInternal()).isFalse();
        assertThat(eventParticipant.isPaid()).isFalse();
        assertThat(eventParticipant.getEventRole()).isEqualTo(eventRole);
    }

    @Test
    void ensureUpdateEventParticipantWorksProperlyWithChangedEventRole() {
        Event e1 = EventFixtures.getEvent1(LocationFixtures.getOutdoorLocation1(PersonFixtures.getPersonFixture1()), Set.of(TagFixtures.getTag1()));
        String token = "token-764376236";
        EventRole eventRole = EventRoleFixture.getEventRole1();
        EventParticipant eventParticipant = EventParticipant.builder()
                .event(e1)
                .person(PersonFixtures.getPersonFixture2())
                .eventRole(eventRole)
                .token(token)
                .createdAt(LocalDateTime.now())
                .paid(true)
                .internal(true)
                .build();
        when(eventParticipantRepository.findByToken(any())).thenReturn(Optional.of(eventParticipant));
        when(eventRoleService.getEventRoleOptional(any())).thenReturn(Optional.of(eventRole));
        UpdateEventParticipantCommand updateEventParticipantCommand = new UpdateEventParticipantCommand(false, false, eventRole.getSlug());
        eventParticipantService.updateEventParticipant(token, updateEventParticipantCommand);
        assertThat(eventParticipant.getEventRole()).isEqualTo(eventRole);
    }

}
