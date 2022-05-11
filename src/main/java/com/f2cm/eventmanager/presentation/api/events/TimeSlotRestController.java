package com.f2cm.eventmanager.presentation.api.events;

import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateTimeSlotCommand;
import com.f2cm.eventmanager.service.dtos.objs.events.TimeSlotDto;
import com.f2cm.eventmanager.service.types.events.TimeSlotService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(TimeSlotRestController.BASE_URL)

@AllArgsConstructor
public class TimeSlotRestController {

    public static final String BASE_URL = "/api/v1/time-slots";

    private TimeSlotService timeSlotService;

    @GetMapping({"", "/"})
    public HttpEntity<List<TimeSlotDto>> getTimeSlots() {
        return ok(timeSlotService.getTimeSlots()
                .stream()
                .map(TimeSlotDto::of)
                .toList()
        );
    }

    @GetMapping("/{token}")
    public HttpEntity<TimeSlotDto> getTimeSlot(@PathVariable String token) {
        return ok(TimeSlotDto.of(timeSlotService.getTimeSlotByToken(token)));
    }

    @DeleteMapping("/{token}")
    public HttpEntity<TimeSlotDto> deleteTimeSlot(@PathVariable String token) {
        timeSlotService.deleteTimeSlot(token);
        return ok().build();
    }

    @DeleteMapping({"", "/"})
    public HttpEntity<List<TimeSlotDto>> deleteTimeSlots() {
        timeSlotService.deleteTimeSlots();
        return ok().build();
    }

    @PostMapping({"", "/"})
    public HttpEntity<TimeSlotDto> createTimeSlot(@RequestBody @Valid CreateTimeSlotCommand createTimeSlotCommand) {
        var timeSlot = timeSlotService.createTimeSlot(createTimeSlotCommand);
        var uri = UriFactory.createUriWithSelfeReference(BASE_URL, timeSlot.getToken());
        return created(uri).body(TimeSlotDto.of(timeSlot));
    }

    @PutMapping("/{token}")
    public HttpEntity<TimeSlotDto> updateTimeSlot(@PathVariable String token, @RequestBody @Valid CreateTimeSlotCommand createTimeSlotCommand) {
        timeSlotService.updateTimeSlot(token, createTimeSlotCommand);
        return ok().build();
    }
}
