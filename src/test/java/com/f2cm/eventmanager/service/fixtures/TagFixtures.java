package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.events.Tag;

public class TagFixtures {
    public static Tag getTag1() {
        return Tag.builder()
                .name("fun")
                .description("some description")
                .build();
    }
}
