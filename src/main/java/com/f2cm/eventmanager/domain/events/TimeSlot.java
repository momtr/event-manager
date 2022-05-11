package com.f2cm.eventmanager.domain.events;

import com.f2cm.eventmanager.domain.people.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Predicate;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Entity
@Table(name = "ts_timeslots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(
            name = "ts_token",
            unique = true,
            nullable = false
    )
    private String token;

    @Column(name = "ts_from")
    private LocalDateTime from;

    @Column(name = "ts_to")
    private LocalDateTime to;

    @Column(name = "ts_name")
    private String name;

    @Column(name = "ts_description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ts_p_contact")
    private Person contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ts_e_event")
    private Event event;

    public boolean hasEvent() {
        return event != null;
    }

    public TimeSlot removeEvent() {
        this.event = null;
        return this;
    }

    public double hoursInBetween() {
        ensureThatTimeSpanExistsAndIsCorrect();
        return ChronoUnit.HOURS.between(from, to);
    }

    public boolean takesPlaceWithinOneDay() {
        ensureThatTimeSpanExistsAndIsCorrect();
        return from.getYear() == to.getYear() && from.getDayOfMonth() == to.getDayOfMonth() && from.getMonth() == to.getMonth();
    }

    public void ensureThatTimeSpanExistsAndIsCorrect() {
        ensureThat(from).isNotNull();
        ensureThat(to).isNotNull();
        ensureThat(from).isBeforeOrEquals(to);
    }

    public static Predicate<TimeSlot> hasContactPerson() {
        return ts -> ts.getContact() != null;
    }

}
