package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.places.Address;
import lombok.Getter;

@Getter
public class AddressTestFixture {
    private final Address florianHome;
    private final Address moritzHome;

    public AddressTestFixture() {
        florianHome = Address.builder()
                .country("Austria")
                .city("Vienna")
                .zipCode("1050")
                .streetNumber("Castelligasse 1/15")
                .build();

        moritzHome = Address.builder()
                .country("Austria")
                .city("Vienna")
                .zipCode("1120")
                .streetNumber("Schnegulastraße 44")
                .build();

    }
}
