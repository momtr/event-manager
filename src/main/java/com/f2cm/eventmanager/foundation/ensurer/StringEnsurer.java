package com.f2cm.eventmanager.foundation.ensurer;

import lombok.Getter;

@Getter
public class StringEnsurer implements Ensurer<StringEnsurer, String>{

    private String value;

    public StringEnsurer(String value) {
        this.value = value;
    }

    public StringEnsurer isNotEmpty() {
        return isNotNull().and().meets(s -> !s.isEmpty(), "String [%s] must not be empty!".formatted(value));
    }

    public StringEnsurer isNotBlank() {
        return isNotNull().and().meets(s -> !s.isBlank(), "String [%s] must not be blank!".formatted(value));
    }

    public StringEnsurer hasLength(int length) {
        return isNotNull().and().meets(s -> s.length() == length, "String [%s] must have a length of %s".formatted(value, length));
    }

    public StringEnsurer hasLengthBetweenInclusive(int min, int max) {
        return isNotNull().and().meets(s -> s.length() >= min && s.length() <= max, "String [%s] must have a length between inclusive %s and %s".formatted(value, min, max));
    }

    public StringEnsurer hasLengthBetweenExclusive(int min, int max) {
        return isNotNull().and().meets(s -> s.length() > min && s.length() < max, "String [%s] must have a length between exclusive %s and %s".formatted(value, min, max));
    }
}
