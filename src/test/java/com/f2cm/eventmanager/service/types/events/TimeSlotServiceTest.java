package com.f2cm.eventmanager.service.types.events;

import com.f2cm.eventmanager.persistence.types.events.TimeSlotRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.fixtures.EventFixtures;
import com.f2cm.eventmanager.service.fixtures.LocationFixtures;
import com.f2cm.eventmanager.service.fixtures.PersonFixtures;
import com.f2cm.eventmanager.service.fixtures.TimeSlotFixtures;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.f2cm.eventmanager.service.types.places.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceTest {

    private TimeSlotService timeSlotService;

    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private PersonService personService;
    @Mock private LocationService locationService;
    @Mock private TokenService tokenService;
    @Mock private EventService eventService;

    @BeforeEach
    void setUp() {
        timeSlotService = new TimeSlotService(
                timeSlotRepository,
                personService,
                locationService,
                tokenService,
                eventService
        );
    }

    @Test
    public void ensureThatCreatingTimeSlotWorks() {
        assertThrows(IllegalArgumentException.class, () -> timeSlotService.createTimeSlot(null));

        var person = PersonFixtures.getPersonFixture1();
        var location = LocationFixtures.getOutdoorLocation1(person);
        var event = EventFixtures.getEvent1(location, Set.of());

        when(tokenService.createNanoIdWithType(any())).thenReturn(TimeSlotFixtures.getTimeSlotToken1());
        when(personService.getPerson(any())).thenReturn(person);
        when(timeSlotRepository.save(any())).thenReturn(TimeSlotFixtures.getTimeSlot1(person, event));

        var timeSlot = timeSlotService.createTimeSlot(TimeSlotFixtures.getCreateTimeSlotCommand1(person.getToken()));
        assertThat(timeSlot.getToken()).isEqualTo(TimeSlotFixtures.getTimeSlotToken1());

        verify(tokenService).createNanoIdWithType(any());
        verify(personService).getPerson(person.getToken());
        verify(timeSlotRepository).save(any());
    }

    @Test
    public void ensureThatUpdatingTimeSlotWorks() {
        assertThrows(IllegalArgumentException.class, () -> timeSlotService.updateTimeSlot(null, null));

        var person = PersonFixtures.getPersonFixture1();
        var location = LocationFixtures.getOutdoorLocation1(person);
        var event = EventFixtures.getEvent1(location, Set.of());

        when(tokenService.createNanoIdWithType(any())).thenReturn(TimeSlotFixtures.getTimeSlotToken1());
        when(personService.getPerson(any())).thenReturn(person);
        when(timeSlotRepository.save(any())).thenReturn(TimeSlotFixtures.getTimeSlot1(person, event));

        var timeSlot = timeSlotService.createTimeSlot(TimeSlotFixtures.getCreateTimeSlotCommand1(person.getToken()));
        assertThat(timeSlot.getToken()).isEqualTo(TimeSlotFixtures.getTimeSlotToken1());

        verify(tokenService).createNanoIdWithType(any());
        verify(personService).getPerson(person.getToken());
        verify(timeSlotRepository).save(any());


        when(personService.getPerson(any())).thenReturn(person);
        when(timeSlotRepository.findByToken(any())).thenReturn(Optional.of(TimeSlotFixtures.getTimeSlot2(person, event)));
        when(timeSlotRepository.save(any())).thenReturn(TimeSlotFixtures.getTimeSlot2(person, event));

        var newTimeSlot = timeSlotService.updateTimeSlot(TimeSlotFixtures.getTimeSlotToken1(), TimeSlotFixtures.getCreateTimeSlotCommand2(person.getToken()));
        assertThat(newTimeSlot.getName()).isEqualTo(TimeSlotFixtures.getTimeSlot2(person, event).getName());

        // no new token should have been created
        verify(tokenService).createNanoIdWithType(any());
        verify(personService, times(2)).getPerson(person.getToken());
        verify(timeSlotRepository, times(2)).save(any());
    }

}
