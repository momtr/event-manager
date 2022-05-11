package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.persistence.types.events.EventRepository;
import com.f2cm.eventmanager.persistence.types.events.TagRepository;
import com.f2cm.eventmanager.persistence.types.events.TimeSlotRepository;
import com.f2cm.eventmanager.persistence.fixtures.EventParticipantTestFixture;
import com.f2cm.eventmanager.persistence.types.places.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventParticipantRepositoryTest {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final PersonRepository personRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final EventRoleRepository eventRoleRepository;
    private final TagRepository tagRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    EventParticipantRepositoryTest(EventParticipantRepository eventParticipantRepository, EventRepository eventRepository, LocationRepository locationRepository, PersonRepository personRepository, ContactTypeRepository contactTypeRepository, EventRoleRepository eventRoleRepository, TagRepository tagRepository, TimeSlotRepository timeSlotRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.personRepository = personRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.eventRoleRepository = eventRoleRepository;
        this.tagRepository = tagRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @BeforeEach
    void beforeEach() {
        EventParticipantTestFixture eventParticipantTestFixture = new EventParticipantTestFixture();
        var personFixture = eventParticipantTestFixture.getPersonTestFixture();
        var eventFixture = eventParticipantTestFixture.getEventTestFixture();
        var eventRoleFixture = eventParticipantTestFixture.getEventRoleTestFixture();
        var tagFixture = eventFixture.getTagTestFixture();
        var locationFixture = eventFixture.getLocationTestFixture();
        var contactTypeFixture = personFixture.getContactTestFixture().getContactTypeTestFixture();
        var timeslotFixture = eventFixture.getTimeSlotTestFixture();

        timeSlotRepository.saveAll(timeslotFixture.getAllTimeSlots());
        tagRepository.saveAll(tagFixture.getAllTags());
        eventRoleRepository.saveAll(eventRoleFixture.getAllEventRoles());
        eventRoleRepository.saveAll(eventRoleFixture.getAllEventRoles());
        contactTypeRepository.saveAll(contactTypeFixture.getAllContactTypes());
        locationRepository.saveAll(locationFixture.getAllLocations());
        personRepository.saveAll(personFixture.getAllPersons());
        eventRepository.saveAll(eventFixture.getAllEvents());
        eventParticipantRepository.saveAll(eventParticipantTestFixture.getAllEventParticipants());
    }

    @Test
    void ensureThatFindByInternalDeliversCorrectResults() {
        var internal = eventParticipantRepository.findByInternal(true);
        var notInternal = eventParticipantRepository.findByInternal(false);

        assertThat(internal.size()).isEqualTo(2);
        assertThat(notInternal.size()).isEqualTo(1);
    }

    @Test
    void ensureThatFindByPaidDeliversCorrectResults() {
        var paid = eventParticipantRepository.findByPaid(true);
        var notPaid = eventParticipantRepository.findByPaid(false);

        assertThat(paid.size()).isEqualTo(2);
        assertThat(notPaid.size()).isEqualTo(1);
    }

    @Test
    void ensureThatFindByPersonDeliversCorrectResults() {
        var floriansEventParticipations = eventParticipantRepository.findByPerson(personRepository.findByFirstNameAndLastName("Florian", "Flatscher").get());
        assertThat(floriansEventParticipations.size()).isEqualTo(1);
    }

}
