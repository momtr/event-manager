package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.domain.people.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    List<EventParticipant> findByEvent(Event event);
    List<EventParticipant> findByEventToken(String event_token);
    List<EventParticipant> findByInternal(boolean internal);
    List<EventParticipant> findByPaid(boolean paid);
    List<EventParticipant> findByPerson(Person person);
    List<EventParticipant> findByPersonAndEvent(Person person, Event event);
    List<EventParticipant> findEventParticipantByPersonToken(String person_token);
    Optional<EventParticipant> findByPersonAndEventAndEventRole(Person person, Event event, EventRole eventRole);
    Optional<EventParticipant> findByToken(String token);
    long countAllByEventRole(EventRole eventRole);
    void deleteEventParticipantByToken(String token);
    void deleteEventParticipantsByEventToken(String event_token);

}
