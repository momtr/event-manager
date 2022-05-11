package com.f2cm.eventmanager.foundation.ensurer;

import lombok.Getter;

@Getter
public class IntEnsurer implements Ensurer<IntEnsurer, Integer> {

    private Integer value;

    public IntEnsurer(Integer value) {
        this.value = value;
    }

    public IntEnsurer isGreaterThan(Integer min) {
        return isNotNull().and().meets(i -> i > min, "Integer [%s] must be greater than %s!".formatted(value, min));
    }

    public IntEnsurer isSmallerThan(Integer max) {
        return isNotNull().and().meets(i -> i < max, "Integer [%s] must be smaller than %s!".formatted(value, max));
    }

    public IntEnsurer isGreaterThanOrEquals(Integer min) {
        return isNotNull().and().meets(i -> i >= min, "Integer [%s] must be greater than or equals %s!".formatted(value, min));
    }

    public IntEnsurer isSmallerThanOrEquals(Integer max) {
        return isNotNull().and().meets(i -> i <= max, "Integer [%s] must be smaller than or equals %s!".formatted(value, max));
    }

    public IntEnsurer isPositiveOrZero() {
        return isNotNull().and().meets(i -> i >= 0, "Integer [%s] must be positive or zero!");
    }

    public IntEnsurer isBetweenInclusive(int min, int max) {
        return isNotNull().and().meets(i -> i >= min && i <= max, "Integer [%s] must have a length between inclusive %s and %s".formatted(value, min, max));
    }

    public IntEnsurer isBetweenExclusive(int min, int max) {
        return isNotNull().and().meets(i -> i > min && i < max, "Integer [%s] must have a length between exclusive %s and %s".formatted(value, min, max));
    }
}
