package com.f2cm.eventmanager.persistence.projections;

import com.f2cm.eventmanager.domain.events.Event;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventsTest {

    @Test
    void ensureGetTotalDurationWorks() {
        int daysDiff = 2;
        Iterable<Event> eventIterable = Stream.of(1,2,3,4).map(item ->
                Event.builder().name("EVENT-" + item).startDate(LocalDate.now().minusDays(daysDiff)).endDate(LocalDate.now()).build()
        ).collect(Collectors.toList());
        Events events = Events.of(Streamable.of(eventIterable));
        assertThat(events.getTotalDurationHours()).isEqualTo(daysDiff * 4 * 24.0);
    }

}
