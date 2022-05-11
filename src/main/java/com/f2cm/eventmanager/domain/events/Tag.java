package com.f2cm.eventmanager.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "t_tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "t_name", unique = true)
    private String name;

    @Column(name = "t_description")
    private String description;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    public Tag clearEvents() {
        this.events.clear();
        return this;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public Tag addEvent(Event event) {
        checkEventNotNull(event);
        if(!hasEvent(event)) {
            this.events.add(event);
        }
        return this;
    }

    public boolean hasEvent(Event event) {
        return this.events.indexOf(event) != -1;
    }

    public Tag removeEvent(Event event) {
        checkEventNotNull(event);
        this.events.remove(event);
        return this;
    }

    public void checkEventNotNull(Event event) {
        if(Objects.isNull(event)) {
            throw new IllegalArgumentException("event must not be null");
        }
    }

}
