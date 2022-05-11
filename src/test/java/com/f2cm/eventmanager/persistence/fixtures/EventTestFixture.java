package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.domain.events.TimeSlot;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class EventTestFixture {
    private final LocationTestFixture locationTestFixture;
    private final TimeSlotTestFixture timeSlotTestFixture;
    private final TagTestFixture tagTestFixture;

    private final Event prom;
    private final Event nightForLegends;
    private final Event conference;

    public EventTestFixture() {
        locationTestFixture = new LocationTestFixture();
        timeSlotTestFixture = new TimeSlotTestFixture();
        tagTestFixture = new TagTestFixture();

        List<TimeSlot> promTimeSlots = new ArrayList<>();
        promTimeSlots.add(timeSlotTestFixture.getMidnightSpecial());

        Set<Tag> promTags = new HashSet<>();
        promTags.add(tagTestFixture.getFreeEntry());
        promTags.add(tagTestFixture.getFreeDrinks());

        prom = Event.builder()
                .token("eventToken1")
                .name("school prom 2022")
                .location(locationTestFixture.getFloriansHome())
                .timeSlots(promTimeSlots)
                .eventType(EventType.PARTY)
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 1, 1))
                .luxury(true)
                .tags(promTags)
                .build();
        tagTestFixture.getFreeDrinks().addEvent(prom);
        tagTestFixture.getFreeEntry().addEvent(prom);
        timeSlotTestFixture.getMidnightSpecial().setEvent(prom);

        nightForLegends = Event.builder()
                .token("eventToken2")
                .name("Night For Legends")
                .location(locationTestFixture.getOutdoorLounge())
                .timeSlots(List.of())
                .eventType(EventType.PARTY)
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 1, 1))
                .luxury(false)
                .build();

        conference = Event.builder()
                .token("eventToken3")
                .name("COVID GECKO CONFERENCE 1")
                .location(locationTestFixture.getFloriansHome())
                .eventType(EventType.CONFERENCE)
                .timeSlots(List.of(timeSlotTestFixture.getTalk()))
                .startDate(LocalDate.of(2021, 4, 16))
                .endDate(LocalDate.of(2021, 4, 16))
                .luxury(true)
                .tags(Set.of(tagTestFixture.getFreeDrinks()))
                .build();
        tagTestFixture.getFreeDrinks().addEvent(conference);
        timeSlotTestFixture.getTalk().setEvent(conference);
    }

    public List<Event> getAllEvents() {
        return List.of(prom, nightForLegends, conference);
    }
}
