package com.f2cm.eventmanager.domain.people;

import com.f2cm.eventmanager.domain.events.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Entity
@Table(name = "ep_eventparticipants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipant extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "ep_token")
    private String token;

    @Column(name = "ep_createdat")
    private LocalDateTime createdAt;

    @Column(name = "ep_ispaid")
    private boolean paid;

    @Column(name = "ep_isinternal")
    private boolean internal;

    @OneToOne
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ep_ev_eventrole")
    private EventRole eventRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_e_event")
    private Event event;

    public static Predicate<EventParticipant> isPaidEmployee() {
        return EventParticipant::isPaid;
    }
    public static Predicate<EventParticipant> isEmployee() {
        return EventParticipant::isInternal;
    }

}
