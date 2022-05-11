package com.f2cm.eventmanager.domain.events;

import com.f2cm.eventmanager.persistence.fixtures.ContactTestFixture;
import com.f2cm.eventmanager.persistence.fixtures.EventTestFixture;
import com.f2cm.eventmanager.persistence.fixtures.PersonTestFixture;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventTest {

    @Test
    void ensureThatClearingTimeSlotsWorks() {
        Event event = new EventTestFixture().getProm();
        TimeSlot first = event.getTimeSlots().get(0);
        event.clearTimeSlots();
        assertThat(event.getTimeSlots().size()).isEqualTo(0);
        assertThat(first.getEvent()).isNull();
    }

    @Test
    void ensureThatAddingTimeSlotsAddsThemToEvent() { // LocalDate.of(2022, 1, 1)
        Event event = new EventTestFixture().getProm().clearTimeSlots();
        TimeSlot ts1 = TimeSlot.builder().name("TS1").from(LocalDateTime.of(2022,1,1,22,30)).to(LocalDateTime.of(2022,1,1,23,30)).build();
        TimeSlot ts2 = TimeSlot.builder().name("TS2").from(LocalDateTime.of(2022,1,1,22,30)).to(LocalDateTime.of(2022,1,1,23,30)).build();
        TimeSlot ts3 = TimeSlot.builder().name("TS3").from(LocalDateTime.of(2022,1,1,22,30)).to(LocalDateTime.of(2022,1,1,23,30)).build();
        event.addTimeSlots(ts1, ts2, ts3);
        assertThrows(IllegalArgumentException.class, () -> {
            event.addTimeSlot(null);
        });
        assertThat(event.getTimeSlots().size()).isEqualTo(3);
        List.of(ts1, ts2, ts3).stream().forEach(ts -> assertThat(ts.getEvent()).isEqualTo(event));
    }

    @Test
    void ensureThatNoTimeSlotsOutOrRangeCanBeAdded() {
        Event event = new EventTestFixture().getProm().clearTimeSlots();
        TimeSlot ts1 = TimeSlot.builder().name("TS1").from(LocalDateTime.now()).to(LocalDateTime.now()).build();
        assertThrows(IllegalArgumentException.class, () -> {
            event.addTimeSlot(ts1);
        });
        assertThat(event.getTimeSlots().size()).isEqualTo(0);
    }

    @Test
    void ensureThatRemovingTimeslotsWork() {
        EventTestFixture eventTestFixture = new EventTestFixture();
        Event event = eventTestFixture.getProm();
        TimeSlot midnightSpecial = eventTestFixture.getTimeSlotTestFixture().getMidnightSpecial();
        event.removeTimeSlots(midnightSpecial);
        assertThat(event.getTimeSlots().size()).isEqualTo(0);
        assertThat(midnightSpecial.hasEvent()).isFalse();
    }

    @Test
    void ensureThatClearingTagsWorks() {
        Event event = new EventTestFixture().getProm();
        event.clearTags();
        assertThat(event.getTags().size()).isEqualTo(0);
    }

    @Test
    void ensureThatAddingTagAddsToTags() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Tag tag1 = Tag.builder().name("Tag-1").build();
        Tag tag2 = Tag.builder().name("Tag-2").build();
        event1.addTag(tag1);
        event1.addTag(tag2);
        assertThat(event1.getTags().size()).isEqualTo(2);
        assertThat(tag1.hasEvent(event1));
        assertThat(tag2.hasEvent(event1));
    }

    @Test
    void ensureThatRemovingTagWorks() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Tag tag1 = Tag.builder().name("Tag-1").build();
        event1.addTag(tag1);
        event1.removeTag(tag1);
        assertThat(event1.getTags().size()).isEqualTo(0);
        assertThat(tag1.getEvents().size()).isEqualTo(0);
    }

    @Test
    void ensureDurationYieldsRightDurationInDaysAndHours() {
        int numOfDaysBetween = 2;
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(numOfDaysBetween)).build();
        assertThat(event1.durationDays()).isEqualTo(numOfDaysBetween);
        assertThat(event1.durationHours()).isEqualTo(numOfDaysBetween * 24);
    }

    @Test
    void ensureGettingUnassignedTimeslotsWorksProperly() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        TimeSlot ts1 = TimeSlot.builder().name("TS-1").from(LocalDateTime.now()).to(LocalDateTime.now()).event(event1).build();
        TimeSlot ts2 = TimeSlot.builder().name("TS-2").from(LocalDateTime.now()).to(LocalDateTime.now()).contact(new PersonTestFixture().getMoritz()).event(event1).build();
        event1.addTimeSlots(ts1, ts2);
        List<TimeSlot> unassignedTimeslots = event1.getUnassignedTimeSlots();
        assertThat(unassignedTimeslots.size()).isEqualTo(1);
        assertThat(unassignedTimeslots.get(0)).isEqualTo(ts2);
    }

}
