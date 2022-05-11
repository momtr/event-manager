package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.persistence.projections.LimitedPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByFirstNameAndLastName(String firstName, String lastName);
    List<Person> findByGender(Gender gender);
    List<Person> findByAddress_City(String city);
    List<LimitedPerson> findTop10ByLastNameStartingWithOrderByLastNameDesc(String lastname);
    Optional<Person> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByToken(String token);

}
