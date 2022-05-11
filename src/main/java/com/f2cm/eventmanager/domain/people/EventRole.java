package com.f2cm.eventmanager.domain.people;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@Entity
@Table(name = "er_eventroles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRole extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "er_slug")
    private String slug;

    @Column(name = "er_createdat")
    private LocalDateTime createdAt;

    @Column(name = "er_name", unique = true)
    private String name;

    public static Predicate<EventRole> isBoss() {
        return er -> er.getName().toLowerCase().startsWith("boss");
    }

}
