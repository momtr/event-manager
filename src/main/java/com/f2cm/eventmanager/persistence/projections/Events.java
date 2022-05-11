package com.f2cm.eventmanager.persistence.projections;

import com.f2cm.eventmanager.domain.events.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;

@RequiredArgsConstructor(staticName = "of")
public class Events implements Streamable<Event> {

    private final Streamable<Event> streamable;

    /**
     * used for accounting purposes (calculating cost) and returns the total duration of all events in the stream
     * @return
     */
    public double getTotalDurationHours() {
        return streamable.stream()
                .map(Event::durationHours)
                .reduce(0.0, (a, b) -> a + b);
    }

    @Override
    public Iterator<Event> iterator() {
        return streamable.iterator();
    }

    public long size() {
        return streamable.stream().count();
    }

}
