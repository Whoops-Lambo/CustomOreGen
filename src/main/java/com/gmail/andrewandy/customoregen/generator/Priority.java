package com.gmail.andrewandy.customoregen.generator;

import java.util.Objects;

/**
 * Represents the priority level of the generator.
 */
public enum Priority {

    LOWEST, LOW, NORMAL, HIGH, HIGHEST;

    public boolean isHighestPriority() {
        return this == HIGHEST;
    }

    public boolean isLowestPriority() {
        return this == LOWEST;
    }

    public Priority getNext() {
        if (isHighestPriority()) {
            return HIGHEST;
        }
        return Priority.values()[this.ordinal() + 1];
    }

    public Priority getPrevious() {
        if (isLowestPriority()) {
            return LOWEST;
        }
        return Priority.values()[this.ordinal() - 1];
    }

    public boolean isHigher(Priority other) {
        return Objects.requireNonNull(other).ordinal() > this.ordinal();
    }

    public boolean isLower(Priority other) {
        return Objects.requireNonNull(other).ordinal() < this.ordinal();
    }
}
