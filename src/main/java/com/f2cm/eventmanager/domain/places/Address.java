package com.f2cm.eventmanager.domain.places;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Version;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String streetNumber;
    private String zipCode;
    private String city;
    private String country;

    public String fullAnonymousAddressText() {
        return new StringBuilder()
                .append(country)
                .append(System.lineSeparator())
                .append(String.format("%s %s", zipCode, city))
                .append(System.lineSeparator())
                .append(streetNumber)
                .append(System.lineSeparator())
                .toString();
    }

}
