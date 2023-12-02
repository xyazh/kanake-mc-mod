package com.xyazh.kanake.recipes.mono;

import java.util.HashSet;

public class NoEqSet<E> extends HashSet<E> {
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }
}
