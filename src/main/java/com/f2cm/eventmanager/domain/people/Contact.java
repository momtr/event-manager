package com.f2cm.eventmanager.domain.people;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Entity
@Table(name = "c_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Contact extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "c_token")
    private String token;

    @Column(name = "c_createdat")
    private LocalDateTime createdAt;

    @Column(name = "c_value")
    private String value;

    @Column(name = "c_isbusiness")
    private boolean business;

    @ManyToOne()
    @JoinColumn(name = "c_ct_contacttype")
    private ContactType contactType;

    public String getContactText() {
        ensureThat(contactType).isNotNull();
        return String.format("%s: %s", contactType.getMeans(), value);
    }

    public static Predicate<Contact> isBusinessContact() {
        return Contact::isBusiness;
    }

}
