package com.f2cm.eventmanager.persistence.types.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.persistence.projections.Events;
import com.f2cm.eventmanager.persistence.projections.LimitedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor {

    List<Event> findByWasHold(boolean wasHold);
    List<Event> findByLocation(Location location);
    List<Event> findByLuxury(boolean luxury);
    List<Event> findByEventType(EventType eventType);

    Optional<Event> findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);

    Collection<LimitedEvent> findByStartDateAfter(LocalDate date);
    Collection<LimitedEvent> findByStartDateAfterAndEndDateBeforeOrderByStartDateDesc(LocalDate fromDate, LocalDate toDate);

    Events findAllByStartDateAfter(LocalDate date);

    @Query("SELECT e FROM Event e JOIN e.tags t where t.name = :tagName")
    List<Event> findEventsWithTagName(@Param("tagName") String tagName);

    @Query("SELECT e FROM Event e JOIN e.tags t where t.name IN :tagNames")
    Set<Event> findEventsByTagNames(@Param("tagNames") String[] tagNames);

    @Query("SELECT e FROM Event e WHERE (e.name LIKE %:searchtext%) OR (e.location.name LIKE %:searchtext%)")
    List<Event> getEventsBySearchString(String searchtext);

}
