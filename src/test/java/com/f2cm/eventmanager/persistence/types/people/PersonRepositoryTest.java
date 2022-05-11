package com.f2cm.eventmanager.persistence.types.people;

import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.persistence.fixtures.PersonTestFixture;
import com.f2cm.eventmanager.persistence.projections.LimitedPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest()
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ContactTypeRepository contactTypeRepository;

    private PersonTestFixture personTestFixture;

    @BeforeEach
    void beforeEach() {
        personTestFixture = new PersonTestFixture();
        personRepository.saveAll(personTestFixture.getAllPersons());
        contactTypeRepository.saveAll(personTestFixture.getContactTestFixture().getContactTypeTestFixture().getAllContactTypes());
    }

    @Test
    void ensureThatFindByFirstNameAndLastNameWorksProperly() {
        Optional<Person> florianOptional = personRepository.findByFirstNameAndLastName("Florian", "Flatscher");
        assertThat(florianOptional.isPresent()).isTrue();
        assertThat(florianOptional.get()).isEqualTo(personTestFixture.getFlorian());

        Optional<Person> maxOptional = personRepository.findByFirstNameAndLastName("Max", "Mustermann");
        assertThat(maxOptional.isPresent()).isFalse();

        Optional<Person> emptyOptional = personRepository.findByFirstNameAndLastName("", "");
        assertThat(emptyOptional.isPresent()).isFalse();

        Optional<Person> nullOptional = personRepository.findByFirstNameAndLastName(null, null);
        assertThat(nullOptional.isPresent()).isFalse();
    }

    @Test
    void ensureThatFindByGenderWorksProperly() {
        List<Person> male = personRepository.findByGender(Gender.MALE);
        List<Person> female = personRepository.findByGender(Gender.FEMALE);
        List<Person> other = personRepository.findByGender(Gender.OTHER);

        assertThat(male.size()).isEqualTo(3);
        assertThat(female.size()).isEqualTo(1);
        assertThat(other.size()).isEqualTo(0);
    }

    @Test
    void ensureThatFindByAddress_CityWorksProperly() {
        List<Person> peopleLivingInVienna = personRepository.findByAddress_City("Vienna");
        List<Person> peopleLivingInGraz = personRepository.findByAddress_City("Graz");
        assertThat(peopleLivingInVienna.size()).isEqualTo(2);
        assertThat(peopleLivingInGraz.size()).isEqualTo(0);
    }

    @Test
    void ensureThatSearchingByLastNameStartPhraseYieldsRightPeople() {
        List<LimitedPerson> peopleWithNameStart = personRepository.findTop10ByLastNameStartingWithOrderByLastNameDesc("PersonLastName");
        assertThat(peopleWithNameStart.size()).isEqualTo(2);
        assertThat(peopleWithNameStart.get(0).getLastName()).startsWith("PersonLastName");
        assertThat(peopleWithNameStart.get(0).getFirstName()).isEqualTo(personTestFixture.getP3().getFirstName());
    }

    @Test
    void ensureThatDeletingPersonByTokenWorks() {
        List<Person> personsBefore = personRepository.findAll();
        int sizeBefore = personsBefore.size();
        personRepository.deleteByToken("TOKEN-123");
        List<Person> personsAfter = personRepository.findAll();
        assertThat(sizeBefore).isEqualTo(personsAfter.size() + 1);
    }

}
