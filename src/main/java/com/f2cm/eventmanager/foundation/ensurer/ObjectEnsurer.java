package com.f2cm.eventmanager.foundation.ensurer;

import lombok.Getter;

@Getter
public class ObjectEnsurer<V> implements Ensurer<ObjectEnsurer<V>, V> {
    private V value;

    public ObjectEnsurer(V value) {
        this.value = value;
    }
}
