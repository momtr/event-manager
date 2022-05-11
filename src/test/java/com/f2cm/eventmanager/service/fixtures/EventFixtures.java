package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class EventFixtures {

    public static CreateEventCommand createEventCommand1(String locationToken, String organizerToken) {
        return CreateEventCommand.builder()
                .name("School Prom")
                .startDate(LocalDate.of(2022, 5, 1))
                .endDate(LocalDate.of(2022, 5, 4))
                .wasHold(false)
                .luxury(true)
                .locationToken(locationToken)
                .organizerToken(organizerToken)
                .eventType(EventType.PARTY)
                .tagNames(List.of("fun", "school"))
                .build();
    }

    public static CreateEventCommand createEventCommand2(String locationToken, String organizerToken) {
        return CreateEventCommand.builder()
                .name("G7")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 2, 2))
                .wasHold(true)
                .luxury(false)
                .locationToken(locationToken)
                .organizerToken(organizerToken)
                .eventType(EventType.PARTY)
                .tagNames(List.of("politics", "serious"))
                .build();
    }

    public static Event getEvent1(Location location, Set<Tag> promTags) {
        return Event.builder()
                .token("eventToken1")
                .name("school prom 2022")
                .location(location)
                .eventType(EventType.PARTY)
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 1, 1))
                .luxury(true)
                .tags(promTags)
                .build();

    }
}
