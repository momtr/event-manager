package com.f2cm.eventmanager.service.dtos.objs.places;

import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class IndoorLocationDto implements Dto {

    private int numOfFloors;
    private int numOfEntrances;
    private int numOfReceptions;
    private int numOfRooms;

    public static IndoorLocationDto of(IndoorLocation indoorLocation) {
        ensureThat(indoorLocation).isNotNull();
        return IndoorLocationDto.builder()
                .numOfFloors(indoorLocation.getNumOfFloors())
                .numOfEntrances(indoorLocation.getNumOfEntrances())
                .numOfReceptions(indoorLocation.getNumOfReceptions())
                .numOfReceptions(indoorLocation.getNumOfRooms())
                .build();
    }

}
