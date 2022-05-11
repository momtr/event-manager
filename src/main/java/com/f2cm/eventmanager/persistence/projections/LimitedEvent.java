package com.f2cm.eventmanager.persistence.projections;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface LimitedEvent {

    Long getId();
    String getName();
    LocalDate getStartDate();
    LocalDate getEndDate();

    default String getShowDescriptiveLine() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        return String.format("%s: %s - %s", getName(), formatter.format(getStartDate()), formatter.format(getEndDate()));
    }

}
