package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.people.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByContactType(ContactType contactType);
    List<Contact> findByBusiness(boolean business);
    long countAllByBusiness(boolean business);
    long countAllByContactType(ContactType contactType);
    Optional<Contact> findByToken(String token);
    void deleteContactByToken(String token);

}
