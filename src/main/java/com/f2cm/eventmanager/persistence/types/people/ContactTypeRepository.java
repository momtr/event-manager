package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Long> {

    Optional<ContactType> findByMeans(String means);
    List<ContactType> findBySocialNetwork(boolean socialNetwork);
    ContactType deleteByMeans(String means);

}
