package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.events.Tag;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TagTestFixture {
    private final Tag freeDrinks;
    private final Tag freeEntry;

    public TagTestFixture() {
        freeDrinks = Tag.builder()
                .name("free drinks")
                .description("free drinks are provided by the host")
                .build();

        freeEntry = Tag.builder()
                .name("free entry")
                .description("no entry fee")
                .build();
    }

    public List<Tag> getAllTags() {
        return List.of(freeDrinks, freeEntry);
    }
}
