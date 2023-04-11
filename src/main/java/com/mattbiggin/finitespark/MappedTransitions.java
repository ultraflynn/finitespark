package com.mattbiggin.finitespark;

import java.util.Collections;
import java.util.Map;

/**
 * Simple state transition table backed by a Map of states, triggering events and the transitions (new state and action)
 * which come next.
 */
record MappedTransitions(Map<State, Map<Event, Transition>> transitions)
        implements StateTransitionTable {
    MappedTransitions(Map<State, Map<Event, Transition>> transitions) {
        this.transitions = Collections.unmodifiableMap(transitions);
    }

    @Override
    public Map<Event, Transition> lookup(State state) {
        return transitions.get(state);
    }
}