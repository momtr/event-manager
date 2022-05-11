package com.f2cm.eventmanager.service.dtos.commands;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTimeSlotCommand {

    @NotNull
    private LocalDateTime from;

    @NotNull
    private LocalDateTime to;

    @NotNull
    private String name;

    private String description;
    private String contactToken;
    private String eventToken;

}
