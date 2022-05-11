package com.f2cm.eventmanager.service.dtos.objs.places;

import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class AddressDto implements Dto {

    private String streetNumber;
    private String zipCode;
    private String city;
    private String country;

    public static AddressDto of(Address address) {
        ensureThat(address).isNotNull();
        return AddressDto.builder()
                .streetNumber(address.getStreetNumber())
                .zipCode(address.getZipCode())
                .city(address.getCity())
                .country(address.getCountry())
                .build();
    }

}
