package com.f2cm.eventmanager.domain.events;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Location;
import lombok.*;
import org.springframework.beans.Mergeable;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Entity
@Table(name = "e_events")
@Data
@EqualsAndHashCode(exclude = {"tags", "timeSlots"}, callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(
            name = "e_token",
            unique = true,
            nullable = false
    )
    private String token;

    @Column(name = "e_name")
    private String name;

    @Column(name = "e_startdate")
    private LocalDate startDate;

    @Column(name = "e_enddate")
    private LocalDate endDate;

    @Column(name = "e_washold")
    private boolean wasHold;

    @Column(name = "e_isluxury")
    private boolean luxury;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "e_p_organizer")
    private Person organizer;

    @Column(name = "e_eventtype")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<TimeSlot> timeSlots = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "eht_event_has_Tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public Event addTimeSlots(TimeSlot... timeSlots) {
        Objects.requireNonNull(timeSlots);
        Arrays.stream(timeSlots).forEach(this::addTimeSlot);
        return this;
    }

    public Event addTimeSlot(TimeSlot timeSlot) {
        ensureThat(timeSlot).isNotNull();
        timeSlot.ensureThatTimeSpanExistsAndIsCorrect();
        ensureThat(timeSlot.getFrom()).isBetweenInclusive(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        ensureThat(timeSlot.getTo()).isBetweenInclusive(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        if (timeSlot.hasEvent()) {
            timeSlot.getEvent().removeTimeSlot(timeSlot);
        }

        timeSlots.add(timeSlot);
        timeSlot.setEvent(this);
        return this;
    }

    public Event removeTimeSlots(TimeSlot... timeSlots) {
        ensureThat(timeSlots).isNotNull();
        Arrays.stream(timeSlots).forEach(this::removeTimeSlot);
        return this;
    }

    public Event removeTimeSlot(TimeSlot timeSlot) {
        ensureThat(timeSlot).isNotNull();
        timeSlots.remove(timeSlot);
        timeSlot.removeEvent();
        return this;
    }

    public Event clearTimeSlots() {
        this.timeSlots.forEach(TimeSlot::removeEvent);
        this.timeSlots.clear();
        return this;
    }

    public List<TimeSlot> getTimeSlots() {
        return Collections.unmodifiableList(timeSlots);
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.clearTimeSlots();
        this.timeSlots.addAll(timeSlots);
    }

    public Event clearTags() {
        this.tags.forEach(t -> t.removeEvent(this));
        this.tags.clear();
        return this;
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean hasTag(Tag tag) {
        ensureThat(tag).isNotNull();
        return this.tags.contains(tag);
    }

    public Event addTag(Tag tag) {
        ensureThat(tag).isNotNull();
        this.tags.add(tag);
        return this;
    }

    public Event addTags(Tag... tags) {
        ensureThat(tags).isNotNull();
        Arrays.stream(tags).forEach(this::addTag);
        return this;
    }

    public Event removeTag(Tag tag) {
        ensureThat(tag).isNotNull();
        tags.remove(tag);
        tag.removeEvent(this);
        return this;
    }

    public double durationDays() {
        ensureThatTimespanIsValid();
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public double durationHours() {
        return durationDays() * 24;
    }

    public List<TimeSlot> getUnassignedTimeSlots() {
        return timeSlots
                .stream()
                .filter(TimeSlot.hasContactPerson())
                .collect(Collectors.toList());
    }

    private void ensureThatTimespanIsValid() {
        ensureThat(startDate).isNotNull("startDate must not be null");
        ensureThat(endDate).isNotNull("endDate must not be null");
        ensureThat(startDate.atStartOfDay()).isBefore(endDate.atTime(LocalTime.MAX));
    }
}
