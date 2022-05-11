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

@Entity
@Table(name = "ct_contacttypes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactType extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "ct_createdat")
    private LocalDateTime createdAt;

    @Column(name = "ct_means", unique = true)
    private String means;

    @Column(name = "ct_issocialnetwork")
    private boolean socialNetwork;

}
