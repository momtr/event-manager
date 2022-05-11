package com.f2cm.eventmanager.foundation;

import org.junit.jupiter.api.Test;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IntEnsurerTest {

    @Test
    void and() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).meets(i -> i == 5).and().isNotNull().and().isGreaterThan(7);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).isGreaterThan(10).and().isNotNull();
        });

        ensureThat(5).meets(i -> i == 5).and().isNotNull().and().isGreaterThan(4);
    }

    @Test
    void isNotNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat((Integer) null).isNotNull();
        });

        ensureThat(1).isNotNull();
    }

    @Test
    void thenAssign() {
        assertThrows(IllegalArgumentException.class, () -> {
            var result = ensureThat((Integer) null).isNotNull().thenAssign();
        });

        var result = ensureThat(1).isNotNull().thenAssign();
        assertThat(result).isEqualTo(1);
    }

    @Test
    void isGreaterThan() {
        assertThrows(IllegalArgumentException.class, () -> {
             ensureThat(5).isGreaterThan(5);
        });

        ensureThat(5).isGreaterThan(1);
    }

    @Test
    void isSmallerThan() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).isSmallerThan(5);
        });

        ensureThat(5).isSmallerThan(7);
    }

    @Test
    void isGreaterThanOrEquals() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).isGreaterThanOrEquals(6);
        });

        ensureThat(5).isGreaterThanOrEquals(5);
    }

    @Test
    void isSmallerThanOrEquals() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).isSmallerThanOrEquals(4);
        });

        ensureThat(5).isSmallerThanOrEquals(5);
    }

    @Test
    void isPositiveOrZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(-1).isPositiveOrZero();
        });

        ensureThat(5).isPositiveOrZero();
        ensureThat(0).isPositiveOrZero();
    }

    @Test
    void isBetweenInclusive() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(-1).isBetweenInclusive(0, 5);
        });

        ensureThat(5).isBetweenInclusive(3,5);
        ensureThat(0).isBetweenInclusive(0,5);
    }

    @Test
    void isBetweenExclusive() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat(5).isBetweenExclusive(0, 5);
        });

        ensureThat(5).isBetweenExclusive(1,6);
        ensureThat(0).isBetweenExclusive(-1,1);
    }
}
