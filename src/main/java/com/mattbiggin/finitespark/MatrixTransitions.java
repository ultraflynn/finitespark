package com.mattbiggin.finitespark;

import java.util.Map;

/**
 * TODO 2 dimensional array with transitions data stored in
 */
record MatrixTransitions(Map<State, Map<Event, Transition>> transitions)
        implements StateTransitionTable {
    @Override
    public Map<Event, Transition> lookup(State state) {
        return null;
    }
}
