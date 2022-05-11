package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.domain.people.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRoleRepository extends JpaRepository<EventRole, Long> {

    Optional<EventRole> findEventRoleByName(String name);
    Optional<EventRole> findBySlug(String slug);
    void deleteBySlug(String slug);

}
