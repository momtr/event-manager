package com.f2cm.eventmanager.service.dtos.objs.places;

import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class OutdoorLocationDto implements Dto {

    private boolean weatherSafe;
    private boolean hasPool;
    private boolean hasCampFire;
    private boolean hasOutdoorStage;
    private int numOfPlaces;

    public static OutdoorLocationDto of(OutdoorLocation outdoorLocation) {
        ensureThat(outdoorLocation).isNotNull();
        return OutdoorLocationDto.builder()
                .weatherSafe(outdoorLocation.isWeatherSafe())
                .hasPool(outdoorLocation.isHasPool())
                .hasCampFire(outdoorLocation.isHasCampFire())
                .hasOutdoorStage(outdoorLocation.isHasOutdoorStage())
                .numOfPlaces(outdoorLocation.getNumOfPlaces())
                .build();
    }

}
