package com.mattbiggin.finitespark;

import java.util.Map;

/**
 * TODO 2 dimensional array with transitions data stored in
 * TODO The challenge now is to create a data structure that makes this method efficient
 */
record MatrixTransitions(Map<State, Map<Event, Transition>> transitions)
        implements StateTransitionTable {
    @Override
    public Map<Event, Transition> lookup(State state) {
        return null;
    }
}
