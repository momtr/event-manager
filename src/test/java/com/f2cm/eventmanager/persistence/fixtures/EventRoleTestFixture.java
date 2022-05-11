package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.people.EventRole;
import lombok.Getter;

import java.util.List;

@Getter
public class EventRoleTestFixture {
    private final EventRole dj;
    private final EventRole guest;
    private final EventRole buttler;

    public EventRoleTestFixture() {
        dj = EventRole.builder()
                .name("dj")
                .build();

        guest = EventRole.builder()
                .name("guest")
                .build();

        buttler = EventRole.builder()
                .name("buttler")
                .build();
    }

    public List<EventRole> getAllEventRoles() {
        return List.of(dj, guest, buttler);
    }
}
