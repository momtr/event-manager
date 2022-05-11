package com.f2cm.eventmanager.domain.events;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagTest {

    @Test
    void ensureClearingEventsWorks() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Tag tag1 = Tag.builder().name("Tag-1").build();
        tag1.addEvent(event1);
        tag1.clearEvents();
        assertThat(tag1.getEvents().size()).isEqualTo(0);
    }

    @Test
    void ensureAddingEventsAddsEventToTag() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Tag tag1 = Tag.builder().name("Tag-1").build();
        tag1.addEvent(event1);
        tag1.addEvent(event1);
        assertThrows(IllegalArgumentException.class, () -> {
            tag1.addEvent(null);
        });
        assertThat(tag1.getEvents().size()).isEqualTo(1);
        assertThat(tag1.getEvents().get(0)).isEqualTo(event1);
    }

    @Test
    void ensureRemovingEventRemovesEventFromTag() {
        Event event1 = Event.builder().name("Event-1").startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        Tag tag1 = Tag.builder().name("Tag-1").build();
        tag1.addEvent(event1);
        tag1.removeEvent(event1);
        assertThat(tag1.getEvents().size()).isEqualTo(0);
    }

}
