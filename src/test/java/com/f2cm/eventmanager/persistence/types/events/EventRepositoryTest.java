package com.f2cm.eventmanager.persistence.types.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.events.QEvent;
import com.f2cm.eventmanager.persistence.fixtures.*;
import com.f2cm.eventmanager.persistence.projections.Events;
import com.f2cm.eventmanager.persistence.projections.LimitedEvent;
import com.f2cm.eventmanager.persistence.types.places.LocationRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@DataJpaTest
class EventRepositoryTest {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final EntityManager entityManager;

    private EventTestFixture eventTestFixture;
    private LocationTestFixture locationTestFixture;
    private TagTestFixture tagTestFixture;
    private TimeSlotTestFixture timeSlotTestFixture;

    @Autowired
    EventRepositoryTest(EventRepository eventRepository, LocationRepository locationRepository, TagRepository tagRepository, TimeSlotRepository timeSlotRepository, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.tagRepository = tagRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        eventTestFixture = new EventTestFixture();
        tagTestFixture = eventTestFixture.getTagTestFixture();
        locationTestFixture = eventTestFixture.getLocationTestFixture();
        timeSlotTestFixture = eventTestFixture.getTimeSlotTestFixture();

        timeSlotRepository.saveAll(timeSlotTestFixture.getAllTimeSlots());
        eventRepository.saveAll(eventTestFixture.getAllEvents());
        tagRepository.saveAll(tagTestFixture.getAllTags());
        locationRepository.saveAll(locationTestFixture.getAllLocations());
    }

    @Test
    void ensureThatFindBySearchTextWorksProperly() {
        var prom = eventRepository.getEventsBySearchString("prom");
        var locationSearchEvent = eventRepository.getEventsBySearchString("Lounge 1");

        Assertions.assertEquals(prom.size(), 1);
        Assertions.assertEquals(locationSearchEvent.size(), 1);
        Assertions.assertEquals(prom.get(0), eventTestFixture.getProm());
        Assertions.assertEquals(locationSearchEvent.get(0), eventTestFixture.getNightForLegends());
    }

    @Test
    void ensureThatFindEventsWithTagNameWorksProperly() {
        var freeDrinks = eventRepository.findEventsWithTagName("free drinks");
        var freeEntry = eventRepository.findEventsWithTagName("free entry");
        var coronaParty = eventRepository.findEventsWithTagName("corona party");

        Assertions.assertEquals(List.of(eventTestFixture.getProm(), eventTestFixture.getConference()), freeDrinks);
        Assertions.assertEquals(List.of(eventTestFixture.getProm()), freeEntry);
        Assertions.assertEquals(List.of(), coronaParty);
    }

    @Test
    void ensureThatQEventWorksProperly() {
        QEvent event = QEvent.event;
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        List<Event> allEvents = (List<Event>) query.from(event).
                where(event.name.length().between(5,50))
                .orderBy(event.name.desc())
                .fetch();

        Assertions.assertEquals(eventTestFixture.getAllEvents(), allEvents);
        List<Event> noEvents = (List<Event>) query.from(event).
                where(event.name.length().between(5,10))
                .orderBy(event.name.desc())
                .fetch();
        Assertions.assertEquals(List.of(), noEvents);
    }

    @Test
    void ensureThatPredicateExecutorWorksOnEventRepo() {
        QEvent event = QEvent.event;
        BooleanExpression isLuxuryEvent = event.luxury.eq(true);
        BooleanExpression isConferenceEvent = event.eventType.eq(EventType.CONFERENCE);
        Iterable<Event> events = eventRepository.findAll(isLuxuryEvent.and(isConferenceEvent));
        Assertions.assertEquals(IterableUtil.sizeOf(events), 1);
        Assertions.assertEquals(events.iterator().next(), eventTestFixture.getConference());
    }

    @Test
    void ensureThatFindingEventsInTimespanYieldsProjection() {
        Collection<LimitedEvent> limitedEvents = eventRepository.findByStartDateAfterAndEndDateBeforeOrderByStartDateDesc(LocalDate.of(1990, 12, 31), LocalDate.of(2021, 12, 31));
        LimitedEvent event = limitedEvents.iterator().next();

        Event conference = eventTestFixture.getConference();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        Assertions.assertEquals(limitedEvents.size(), 1);
        Assertions.assertEquals(event.getEndDate(), conference.getEndDate());
        Assertions.assertEquals(event.getStartDate(), conference.getStartDate());
        Assertions.assertEquals(event.getName(), conference.getName());
        Assertions.assertNotEquals(event.getEndDate(), null);
        Assertions.assertNotEquals(event.getStartDate(), null);
        Assertions.assertEquals(event.getShowDescriptiveLine(),
                conference.getName() + ": " + formatter.format(conference.getStartDate()) + " - " +
                        formatter.format(conference.getEndDate()));
    }

    @Test
    void ensureFindingAllByStartDateAfterWorksProperlyUsingStreamableProjection() {
        Events events1 = eventRepository.findAllByStartDateAfter(LocalDate.of(1999, 12, 31));
        Events events2 = eventRepository.findAllByStartDateAfter(LocalDate.of(2025, 12, 31));
        Assertions.assertEquals(events1.size(), 3L);
        Assertions.assertEquals(events2.size(), 0L);
    }

}
