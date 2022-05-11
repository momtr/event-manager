package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.persistence.fixtures.ContactTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactTypeRepository contactTypeRepository;

    @BeforeEach
    void setUp() {
        ContactTestFixture contactTestFixture = new ContactTestFixture();
        contactTypeRepository.saveAll(contactTestFixture.getContactTypeTestFixture().getAllContactTypes());
        contactRepository.saveAll(contactTestFixture.getAllContacts());
    }

    @Test
    void ensureThatFindByContactTypeDeliversCorrectResults() {
        var allContacts = contactRepository.findAll();
        System.out.println("allContacts = " + allContacts);
        var snapchatContacts = contactRepository.findByContactType(contactTypeRepository.findByMeans("snapchat").orElseThrow());
        var phoneContacts = contactRepository.findByContactType(contactTypeRepository.findByMeans("phone").orElseThrow());
        assertThat(allContacts.size()).isEqualTo(3);
        assertThat(snapchatContacts.size()).isEqualTo(2);
        assertThat(phoneContacts.size()).isEqualTo(1);
    }

    @Test
    void ensureThatQueryingByBusinessContactWorks() {
        var businessContacts = contactRepository.findByBusiness(true);
        var notBusinessContacts = contactRepository.findByBusiness(false);
        assertThat(businessContacts.size()).isEqualTo(1);
        assertThat(notBusinessContacts.size()).isEqualTo(2);
    }
}
