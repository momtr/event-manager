package com.f2cm.eventmanager.service.dtos.objs.events;

import com.f2cm.eventmanager.persistence.projections.Events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventsOverviewDto {

    private long numOfEvents;
    private double totalDurationOfEventsInHours;

    public static EventsOverviewDto of(Events events) {
        return EventsOverviewDto.builder()
                .numOfEvents(events.size())
                .totalDurationOfEventsInHours(events.getTotalDurationHours())
                .build();
    }

}
