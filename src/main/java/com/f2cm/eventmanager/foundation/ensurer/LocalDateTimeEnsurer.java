package com.f2cm.eventmanager.foundation.ensurer;

import lombok.Getter;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
public class LocalDateTimeEnsurer implements Ensurer<LocalDateTimeEnsurer, LocalDateTime> {
    private LocalDateTime value;

    public LocalDateTimeEnsurer(LocalDateTime value) {
        this.value = value;
    }

    public LocalDateTimeEnsurer isBefore(LocalDateTime other) {
        ensureThat(other).isNotNull("other LocalDateTime must not be null");
        return isNotNull().and().meets((d) -> d.isBefore(other), "LocalDateTime [%s] must be before %s".formatted(value, other));
    }

    public LocalDateTimeEnsurer isBeforeOrEquals(LocalDateTime other) {
        ensureThat(other).isNotNull("other LocalDateTime must not be null");
        return isNotNull().and().meets((d) -> d.isBefore(other) || d.isEqual(other), "LocalDateTime [%s] must be before or equals%s".formatted(value, other));
    }

    public LocalDateTimeEnsurer isBetweenInclusive(LocalDateTime start, LocalDateTime end) {
        ensureThat(start).isNotNull("other LocalDateTime must not be null");
        ensureThat(end).isNotNull("other LocalDateTime must not be null");

        return isNotNull().and()
                .meets((d) -> (d.getMonth().getValue() >= start.getMonth().getValue() && d.getMonth().getValue() <= end.getMonth().getValue()) &&
                                (d.getYear() >= start.getYear() && d.getYear() <= end.getYear()) &&
                                (d.getDayOfMonth() >= start.getDayOfMonth() && d.getDayOfMonth() <= end.getDayOfMonth()),
                        "LocalDateTime [%s] must be between inclusive %s and %s".formatted(value, start, end)
                );
    }
}
