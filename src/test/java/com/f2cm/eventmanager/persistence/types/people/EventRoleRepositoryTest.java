package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.persistence.fixtures.EventRoleTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class EventRoleRepositoryTest {

    @Autowired
    private EventRoleRepository eventRoleRepository;

    @BeforeEach
    void setUp() {
        EventRoleTestFixture eventRoleTestFixture = new EventRoleTestFixture();
        eventRoleRepository.saveAll(eventRoleTestFixture.getAllEventRoles());
    }

    @Test
    void ensureThatFindEventRoleByNameDeliversCorrectResults() {
        var dj = eventRoleRepository.findEventRoleByName("dj");
        assertThat(dj).isNotNull();
    }
}
