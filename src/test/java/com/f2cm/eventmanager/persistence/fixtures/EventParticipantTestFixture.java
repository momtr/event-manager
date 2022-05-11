package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.people.EventParticipant;
import lombok.Getter;

import java.util.List;

@Getter
public class EventParticipantTestFixture {
    private final EventRoleTestFixture eventRoleTestFixture;
    private final PersonTestFixture personTestFixture;
    private final EventTestFixture eventTestFixture;
    private final EventParticipant djMoritz;
    private final EventParticipant buttlerMoritz;
    private final EventParticipant guestFlorian;

    public EventParticipantTestFixture() {
        eventRoleTestFixture = new EventRoleTestFixture();
        personTestFixture = new PersonTestFixture();
        eventTestFixture = new EventTestFixture();

        djMoritz = EventParticipant.builder()
                .event(eventTestFixture.getProm())
                .person(personTestFixture.getMoritz())
                .eventRole(eventRoleTestFixture.getDj())
                .internal(true)
                .paid(true)
                .build();

        buttlerMoritz = EventParticipant.builder()
                .event(eventTestFixture.getProm())
                .person(personTestFixture.getMoritz())
                .eventRole(eventRoleTestFixture.getButtler())
                .internal(true)
                .paid(true)
                .build();

        guestFlorian = EventParticipant.builder()
                .event(eventTestFixture.getProm())
                .person(personTestFixture.getFlorian())
                .eventRole(eventRoleTestFixture.getGuest())
                .build();
    }

    public List<EventParticipant> getAllEventParticipants() {
        return List.of(djMoritz, buttlerMoritz, guestFlorian);
    }
}
