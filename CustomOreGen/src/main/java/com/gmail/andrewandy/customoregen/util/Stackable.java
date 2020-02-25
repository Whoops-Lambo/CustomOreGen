package com.gmail.andrewandy.customoregen.util;

import java.util.Collection;

public interface Stackable<T> {

    Collection<StackedObject<T>> getStacked();

    boolean canStack(StackedObject<T> stackedObject);

    void stack(StackedObject<T> stackedObject);

    int maxSize();

    default int size() {
        return getStacked().size();
    }

    default boolean isFull() {
        return size() < maxSize();
    }

    StackedObject<T> convertToStacked();

    class StackedObject<T> {
        private final T original;

        public StackedObject(T original) {
            this.original = original;
        }

        public T getOriginal() {
            return original;
        }
    }

}
