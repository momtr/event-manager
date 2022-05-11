package com.f2cm.eventmanager.foundation.ensurer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;
import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeEnsurerTest {

    @Test
    void isBefore() {
        assertThrows(IllegalArgumentException.class, () -> {
           ensureThat(LocalDateTime.MAX).isBefore(LocalDateTime.MIN);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat((LocalDateTime) null).isBefore(LocalDateTime.MAX);
        });

        ensureThat(LocalDateTime.of(2000, 10, 10, 0, 0)).isBefore(LocalDateTime.of(2022, 10, 10, 0, 0));
    }

    @Test
    void isBeforeOrEquals() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(LocalDateTime.MAX).isBeforeOrEquals(LocalDateTime.MIN);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat((LocalDateTime) null).isBeforeOrEquals(LocalDateTime.MAX);
        });

        ensureThat(LocalDateTime.of(2000, 10, 10, 0, 0)).isBeforeOrEquals(LocalDateTime.of(2022, 10, 10, 0, 0));
        ensureThat(LocalDateTime.of(2000, 10, 10, 0, 0)).isBeforeOrEquals(LocalDateTime.of(2000, 10, 10, 0, 0));
    }
}
