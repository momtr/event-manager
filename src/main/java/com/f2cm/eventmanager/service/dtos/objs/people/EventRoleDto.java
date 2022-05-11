package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class EventRoleDto implements Dto {

    private String _slug;
    private LocalDateTime _createdAt;
    private String name;

    public static EventRoleDto of(EventRole eventRole) {
        ensureThat(eventRole).isNotNull();
        return EventRoleDto.builder()
                ._createdAt(eventRole.getCreatedAt())
                ._slug(eventRole.getSlug())
                .name(eventRole.getName())
                .build();
    }

}
