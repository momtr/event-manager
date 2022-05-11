package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.Contact;
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
public class ContactDto implements Dto {

    private String _token;
    private LocalDateTime _createdAt;
    private String value;
    private boolean isBusiness;
    private String type;

    public static ContactDto of(Contact contact) {
        ensureThat(contact).isNotNull();
        return ContactDto.builder()
                ._token(contact.getToken())
                ._createdAt(contact.getCreatedAt())
                .value(contact.getValue())
                .isBusiness(contact.isBusiness())
                .type(contact.getContactType().getMeans())
                .build();
    }

}
