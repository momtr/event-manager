package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.people.Contact;
import lombok.Getter;

import java.util.List;

@Getter
public class ContactTestFixture {
    private final ContactTypeTestFixture contactTypeTestFixture;

    private final Contact florianPhone;
    private final Contact florianSnapchat;
    private final Contact moritzSnapchat;

    public ContactTestFixture() {
        contactTypeTestFixture = new ContactTypeTestFixture();

        florianPhone = Contact.builder()
                .contactType(contactTypeTestFixture.getPhone())
                .value("+4315112351")
                .business(true)
                .build();

        florianSnapchat = Contact.builder()
                .contactType(contactTypeTestFixture.getSnapChat())
                .value("bookReader#123")
                .build();

        moritzSnapchat = Contact.builder()
                .contactType(contactTypeTestFixture.getSnapChat())
                .value("destroyer333")
                .build();
    }

    public List<Contact> getAllContacts() {
        return List.of(florianPhone, florianSnapchat, moritzSnapchat);
    }
}
