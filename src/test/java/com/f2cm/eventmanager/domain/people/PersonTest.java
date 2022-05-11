package com.f2cm.eventmanager.domain.people;

import com.f2cm.eventmanager.persistence.fixtures.PersonTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PersonTest {

    @Test
    void ensureAgeCalculationIsCorrect() {
        PersonTestFixture ptf = new PersonTestFixture();

        Person moritz = ptf.getMoritz();
        Person florian = ptf.getFlorian();

        var ageMoritz = moritz.calculateAge(LocalDate.of(2021, 12, 9));
        var ageFlorian = florian.calculateAge(LocalDate.of(2022, 2, 4));
        assertThat(ageMoritz).isEqualTo(17);
        assertThat(ageFlorian).isEqualTo(18);
        assertThrows(IllegalArgumentException.class, () -> {
            var ageShouldThrow = florian.calculateAge(null);
        });
    }

    @Test
    void ensureFullAddressTextIsCreatedCorrectly() {
        Person person = new PersonTestFixture().getFlorian();
        assertThat(person.getFullAddressText()).isEqualTo(
                "Dipl-Ing. Florian Flatscher, BACK" + System.lineSeparator() +
                "Austria" + System.lineSeparator() +
                "1050 Vienna" + System.lineSeparator() +
                "Castelligasse 1/15" + System.lineSeparator()
        );
    }

    @Test
    void ensureFullOfficialNameIsCreatedCorrectly() {
        Person person = new PersonTestFixture().getMoritz();
        assertThat(person.getFullOfficialName()).isEqualTo("Moritz Mitterdorfer");
    }

    @Test
    void ensureClearingContactsWorks() {
        Person person = new PersonTestFixture().getFlorian();
        int numOfContactsBefore = person.getContacts().size();
        person.clearContacts();
        int numOfContactsAfter = person.getContacts().size();
        assertThat(numOfContactsAfter - numOfContactsBefore).isEqualTo(- numOfContactsBefore);
        assertThat(person.getPrimaryContact()).isNull();
    }

    @Test
    void ensureRemovingContactWorks() {
        PersonTestFixture pft = new PersonTestFixture();
        Person person = pft.getFlorian();
        Contact contact = pft.getContactTestFixture().getFlorianPhone();
        int numOfContactsBefore = person.getContacts().size();
        person.removeContact(contact);
        int numOfContactsAfter = person.getContacts().size();
        assertThat(numOfContactsAfter).isEqualTo(numOfContactsBefore - 1);
        assertThat(person.getContacts().iterator().next()).isNotEqualTo(contact);
        assertThat(person.getContacts().iterator().next()).isEqualTo(pft.getContactTestFixture().getFlorianSnapchat());
    }

    @Test
    void ensureAddingPrimaryContactAddsItToContactSet() {
        PersonTestFixture pft = new PersonTestFixture();
        Person person = pft.getFlorian();
        var emailContactType = ContactType.builder().means("email").socialNetwork(false).build();
        Contact contact = Contact.builder().contactType(emailContactType).value("florian@flatscher.at").business(true).build();
        int numOfContactsBefore = person.getContacts().size();
        assertThat(person.addPrimaryContact(contact.getContactType(), contact.getValue(), contact.isBusiness()));
        assertThat(person.getPrimaryContact()).isEqualTo(contact);
        assertThat(person.getContacts().size()).isEqualTo(numOfContactsBefore + 1);
    }

    @Test
    void ensureRetrievingBusinessContactsWorksProperly() {
        PersonTestFixture pft = new PersonTestFixture();
        Person person = pft.getFlorian();
        Set<Contact> businessContacts = person.getAllBusinessContacts();
        assertThat(businessContacts.size()).isEqualTo(1);
        assertThat(businessContacts.iterator().next()).isEqualTo(pft.getContactTestFixture().getFlorianPhone());
    }

}
