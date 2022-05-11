package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ContactTypeDto implements Dto {

    private LocalDateTime _createdAt;
    private String means;
    private boolean isSocialNetwork;

    public static ContactTypeDto of(ContactType contactType) {
        ensureThat(contactType).isNotNull();
        return ContactTypeDto.builder()
                ._createdAt(contactType.getCreatedAt())
                .means(contactType.getMeans())
                .isSocialNetwork(contactType.isSocialNetwork())
                .build();
    }

}
