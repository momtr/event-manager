package com.f2cm.eventmanager.persistence.types.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.domain.people.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    @Query("SELECT t from TimeSlot t WHERE :time BETWEEN t.from AND t.to")
    List<TimeSlot> getTimeSlotsAt(@Param("time") LocalDateTime time);

    List<TimeSlot> findByName(String name);
    List<TimeSlot> findByContact(Person contact);
    List<TimeSlot> findByEvent(Event event);
    List<TimeSlot> findByEventToken(String event_token);

    Optional<TimeSlot> findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);

}
