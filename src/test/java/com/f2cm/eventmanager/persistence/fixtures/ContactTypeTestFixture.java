package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.people.ContactType;
import lombok.Getter;

import java.util.List;

@Getter
public class ContactTypeTestFixture {
    private final ContactType phone;
    private final ContactType snapChat;

    public ContactTypeTestFixture() {
        phone = ContactType.builder()
                .means("phone")
                .build();

        snapChat = ContactType.builder()
                .means("snapchat")
                .socialNetwork(true)
                .build();
    }

    public List<ContactType> getAllContactTypes() {
        return List.of(phone, snapChat);
    }
}
