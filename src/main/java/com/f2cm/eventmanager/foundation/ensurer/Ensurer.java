package com.f2cm.eventmanager.foundation.ensurer;

import java.util.function.Predicate;

public interface Ensurer<E extends Ensurer<E, V>, V> {

    V getValue();

    default E and() {
        return (E) this;
    }

    default E meets(Predicate<V> condition) {
        if (!condition.test(getValue())) {
            throw new IllegalArgumentException("value does not meet condition!");
        }

        return and();
    }

    default E meets(Predicate<V> condition, String description) {
        if (!condition.test(getValue())) {
            throw new IllegalArgumentException(description);
        }

        return and();
    }

    default E isNotNull() {
        if (getValue() == null) {
            throw new IllegalArgumentException("value must not be null!");
        }

        return and();
    }

    default E isNotNull(String description) {
        if (getValue() == null) {
            throw new IllegalArgumentException(description);
        }

        return and();
    }

    default V thenAssign() {
        return getValue();
    }
}
