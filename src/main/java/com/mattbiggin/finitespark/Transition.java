package com.mattbiggin.finitespark;

import java.util.Objects;
import java.util.function.Consumer;

record Transition(State state, Consumer<Event> action) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
