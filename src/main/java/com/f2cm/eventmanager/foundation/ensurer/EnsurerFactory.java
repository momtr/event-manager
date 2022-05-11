package com.f2cm.eventmanager.foundation.ensurer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnsurerFactory {

    public static <V> ObjectEnsurer<V> ensureThat(V value) {
        return new ObjectEnsurer<V>(value);
    }

    public static StringEnsurer ensureThat(String value) {
        return new StringEnsurer(value);
    }

    public static IntEnsurer ensureThat(Integer value) {
        return new IntEnsurer(value);
    }

    public static BooleanEnsurer ensureThat(Boolean value) {
        return new BooleanEnsurer(value);
    }

    public static LocalDateTimeEnsurer ensureThat(LocalDateTime value) {
        return new LocalDateTimeEnsurer(value);
    }
}
