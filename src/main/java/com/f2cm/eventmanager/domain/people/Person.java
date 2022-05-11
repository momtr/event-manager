package com.f2cm.eventmanager.domain.people;

import com.f2cm.eventmanager.domain.places.Address;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Entity
@Table(name = "p_persons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(name = "p_token")
    private String token;

    @Column(name = "p_createdat")
    private LocalDateTime createdAt;

    @Column(name = "p_firstname")
    private String firstName;

    @Column(name = "p_lastname")
    private String lastName;

    @Column(name = "p_titlefront")
    private String titleFront;

    @Column(name = "p_titleback")
    private String titleBack;

    @Column(name = "p_birthday")
    private LocalDate birthday;

    @Column(name = "p_mainjob")
    private String mainJob;

    @Column(name = "p_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private Set<Contact> contacts = new HashSet<>();

    @OneToOne
    private Contact primaryContact;

    public int calculateAge(LocalDate currentDate) {
        ensureThat(currentDate).isNotNull();
        return Period.between(getBirthday(), currentDate).getYears();
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getFullOfficialName() {
        String name = ensureThat(getName()).isNotNull().thenAssign();
        if(!Strings.isNullOrEmpty(titleFront))
            name = titleFront + " " + name;
        if(!Strings.isNullOrEmpty(titleBack))
            name += ", " + titleBack;
        return name;
    }

    public String getFullAddressText() {
        return new StringBuilder()
                .append(getFullOfficialName())
                .append(System.lineSeparator())
                .append(address.fullAnonymousAddressText())
                .toString();
    }

    public Person clearContacts() {
        this.contacts.clear();
        this.primaryContact = null;
        return this;
    }

    public Person removeContact(Contact contact) {
        ensureThat(contact).isNotNull();
        this.contacts.remove(contact);
        if(contact.equals(primaryContact))
            this.primaryContact = null;
        return this;
    }

    public Set<Contact> getContacts() {
        return Collections.unmodifiableSet(contacts);
    }

    public Person addPrimaryContact(ContactType contactType, String value, boolean isBusiness) {
        Contact contact = addContact(contactType, value, isBusiness);
        this.primaryContact = contact;
        return this;
    }

    public Contact addContact(ContactType contactType, String value, boolean isBusiness) {
        ensureThat(contactType).isNotNull("contactType must not be null");
        ensureThat(value).isNotNull("value must not be null");
        ensureThat(isBusiness).isNotNull("isBusiness must not be null");

        Contact contact = Contact.builder()
                .contactType(contactType)
                .value(value)
                .business(isBusiness)
                .build();
        this.contacts.add(contact);
        return contact;
    }

    public boolean addContact(Contact contact) {
        ensureThat(contact).isNotNull("contact must not be null");
        return contacts.add(contact);
    }

    public Contact addBusinessContact(ContactType contactType, String value) {
        return addContact(contactType, value, true);
    }

    public Contact addPrivateContact(ContactType contactType, String value) {
        return addContact(contactType, value, false);
    }

    public Set<Contact> getAllBusinessContacts() {
        return contacts
                .stream()
                .filter(Contact.isBusinessContact())
                .collect(Collectors.toSet());
    }

}
