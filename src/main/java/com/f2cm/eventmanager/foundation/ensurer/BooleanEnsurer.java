package com.f2cm.eventmanager.foundation.ensurer;

import lombok.Getter;

@Getter
public class BooleanEnsurer implements Ensurer<BooleanEnsurer, Boolean> {
    private Boolean value;

    public BooleanEnsurer(Boolean value) {
        this.value = value;
    }

    private BooleanEnsurer isTrue() {
        return isNotNull().meets((b) -> b, "value must be true!");
    }

    private BooleanEnsurer isFalse() {
        return isNotNull().meets((b) -> !b, "value must be false!");
    }

    private BooleanEnsurer isTrue(String description) {
        return isNotNull().meets((b) -> b, description);
    }

    private BooleanEnsurer isFalse(String description) {
        return isNotNull().meets((b) -> !b, description);
    }
}
