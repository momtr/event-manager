package com.f2cm.eventmanager.service.dtos.objs.events;

import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class TagDto implements Dto {

    private String name;
    private String description;

    public static TagDto of(Tag tag) {
        ensureThat(tag).isNotNull();
        return TagDto.builder()
                .name(tag.getName())
                .description(tag.getDescription())
                .build();
    }

}
