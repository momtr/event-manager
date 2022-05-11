package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.persistence.fixtures.ContactTypeTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ContactTypeRepositoryTest {

    @Autowired
    private ContactTypeRepository contactTypeRepository;

    @BeforeEach
    void beforeEach() {
        ContactTypeTestFixture contactTypeTestFixture = new ContactTypeTestFixture();
        contactTypeRepository.saveAll(contactTypeTestFixture.getAllContactTypes());
    }

    @Test
    void ensureThatFindByMeansDeliversCorrectResults() {
        var snapChatContactType = contactTypeRepository.findByMeans("snapchat");
        assertThat(snapChatContactType).isNotNull();
    }

    @Test
    void ensureThatFindBySocialNetworkDevliversCOrrectResults() {
        var socialNetworkContacts = contactTypeRepository.findBySocialNetwork(true);
        var notSocialNetworkContacts = contactTypeRepository.findBySocialNetwork(false);
        assertThat(socialNetworkContacts.size()).isEqualTo(1);
        assertThat(notSocialNetworkContacts.size()).isEqualTo(1);
    }
}
