package com.mattbiggin.finitespark;

import java.util.Map;

interface StateTransitionTable {
    Map<Event, Transition> lookup(State state);
}
