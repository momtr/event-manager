package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.people.EventRole;

public class EventRoleFixture {

    public static EventRole getEventRole1() {
        return EventRole.builder()
                .name("even-12Xz")
                .slug("event-role-name")
                .build();
    }

    public static EventRole getEventRole2() {
        return EventRole.builder()
                .name("The New Manager Role")
                .slug("even-abcd")
                .build();
    }

}
