package com.f2cm.eventmanager.service.types.events;

import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.persistence.types.events.EventRepository;
import com.f2cm.eventmanager.persistence.types.events.TagRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.fixtures.EventFixtures;
import com.f2cm.eventmanager.service.fixtures.LocationFixtures;
import com.f2cm.eventmanager.service.fixtures.PersonFixtures;
import com.f2cm.eventmanager.service.fixtures.TagFixtures;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.f2cm.eventmanager.service.types.places.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PersonService personService;
    @Mock
    private LocationService locationService;
    @Mock
    private TokenService tokenService;

    private EventService eventService;

    @BeforeEach
    void beforeEach() {
        eventService = new EventService(
                eventRepository,
                tagRepository,
                personService,
                locationService,
                tokenService
        );
    }

    @Test
    void ensureThatReplacingEventWorks() {
        var person1 = PersonFixtures.getPersonFixture1();
        var person2 = PersonFixtures.getPersonFixture2();

        var outdoorLocation1 = LocationFixtures.getOutdoorLocation1(person1);
        var outdoorLocation2 = LocationFixtures.getOutdoorLocation1(person2);

        var event1 = EventFixtures.getEvent1(outdoorLocation1, new HashSet<>());

        // Create an event
        var createEventCommand = EventFixtures.createEventCommand1(outdoorLocation1.getToken(), person1.getToken());

        when(tokenService.createNanoIdWithType(any())).thenReturn(event1.getToken());
        when(locationService.getLocation(any())).thenReturn(outdoorLocation1);
        when(personService.getPerson(any())).thenReturn(person1);
        when(tagRepository.save(any())).then((InvocationOnMock invocation) -> invocation.getArgument(0));

        var event = eventService.createEvent(createEventCommand);

        assertThat(event.getToken()).isEqualTo(event1.getToken());
        assertThat(event.getLocation().getToken()).isEqualTo(outdoorLocation1.getToken());
        assertThat(event.getOrganizer().getToken()).isEqualTo(person1.getToken());

        // Replace it
        var newCreateEventCommand = EventFixtures.createEventCommand2(outdoorLocation2.getToken(), outdoorLocation2.getToken());

        when(locationService.getLocation(any())).thenReturn(outdoorLocation2);
        when(personService.getPerson(any())).thenReturn(person2);
        when(eventRepository.findByToken(any())).thenReturn(Optional.of(event1));

        var newEvent = eventService.updateEvent(event1.getToken(), newCreateEventCommand);

        assertThat(newEvent.getToken()).isEqualTo(event1.getToken());
        assertThat(newEvent.getLocation().getToken()).isEqualTo(outdoorLocation2.getToken());
        assertThat(newEvent.getOrganizer().getToken()).isEqualTo(person2.getToken());

        verify(eventRepository).findByToken("eventToken1");
        verify(eventRepository, never()).deleteByToken(any());

        // Replace an event that doesn't exist
        when(eventRepository.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.updateEvent("aNonExistentToken", newCreateEventCommand));
    }

    @Test
    void ensureThatCreatingEventWorks() {
        var person1 = PersonFixtures.getPersonFixture1();
        var person2 = PersonFixtures.getPersonFixture2();

        var outdoorLocation1 = LocationFixtures.getOutdoorLocation1(person1);

        var createEventCommand = EventFixtures.createEventCommand1("locationT", "TOKEN-DEF-1");

        when(tokenService.createNanoIdWithType(any())).thenReturn("newEventT");
        when(locationService.getLocation(any())).thenReturn(outdoorLocation1);
        when(personService.getPerson(any())).thenReturn(person1);
        when(tagRepository.save(any())).then((InvocationOnMock invocation) -> invocation.getArgument(0));

        var event = eventService.createEvent(createEventCommand);

        assertThat(event.getToken()).isEqualTo("newEventT");
        assertThat(event.getLocation().getToken()).isEqualTo("outdoorLocationT");
        assertThat(event.getOrganizer().getToken()).isEqualTo("TOKEN-DEF-1");

        verify(tokenService).createNanoIdWithType(any());
        verify(locationService).getLocation("locationT");
        verify(personService).getPerson("TOKEN-DEF-1");
    }

    @Test
    void ensureThatTaggingEventWorks() {
        var person1 = PersonFixtures.getPersonFixture1();
        var person2 = PersonFixtures.getPersonFixture2();

        var outdoorLocation1 = LocationFixtures.getOutdoorLocation1(person1);

        var event1 = EventFixtures.getEvent1(outdoorLocation1, new HashSet<>());

        var createEventCommand = EventFixtures.createEventCommand1("locationT", "TOKEN-DEF-1");

        when(tokenService.createNanoIdWithType(any())).thenReturn("newEventT");
        when(locationService.getLocation(any())).thenReturn(outdoorLocation1);
        when(personService.getPerson(any())).thenReturn(person1);
        when(tagRepository.findByName("fun")).thenReturn(Optional.ofNullable(TagFixtures.getTag1()));
        when(tagRepository.findByName("school")).thenReturn(Optional.empty());
        when(tagRepository.save(any())).then((InvocationOnMock invocation) -> invocation.getArgument(0));

        var event = eventService.createEvent(createEventCommand);

        verify(tagRepository).findByName("fun");
        verify(tagRepository).findByName("school");
        verify(tagRepository, never()).save(Tag.builder().name("fun").build());
        verify(tagRepository).save(Tag.builder().name("school").build());

        when(eventRepository.findByToken("locationT")).thenReturn(Optional.of(event1));

        eventService.removeTagFromEvent("locationT", "fun");

        verify(eventRepository, times(2)).save(any());
    }
}
