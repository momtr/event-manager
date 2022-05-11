package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import com.f2cm.eventmanager.service.dtos.objs.events.SimpleEventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class
EventParticipantDto implements Dto {

    private String _token;
    private LocalDateTime _createdAt;
    private boolean paid;
    private boolean internal;
    private SimplePersonDto person;
    private SimpleEventDto event;
    private EventRoleDto eventRole;

    public static EventParticipantDto of(EventParticipant eventParticipant) {
        ensureThat(eventParticipant).isNotNull();
        return EventParticipantDto.builder()
                ._token(eventParticipant.getToken())
                ._createdAt(eventParticipant.getCreatedAt())
                .paid(eventParticipant.isPaid())
                .internal(eventParticipant.isInternal())
                .person(eventParticipant.getPerson() != null ? SimplePersonDto.of(eventParticipant.getPerson()) : null)
                .event(eventParticipant.getEvent() != null ? SimpleEventDto.of(eventParticipant.getEvent()) : null)
                .eventRole(eventParticipant.getEventRole() != null ? EventRoleDto.of(eventParticipant.getEventRole()) : null)
                .build();
    }

}
