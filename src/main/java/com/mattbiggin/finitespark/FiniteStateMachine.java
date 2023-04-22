package com.mattbiggin.finitespark;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableMap;

public class FiniteStateMachine {
    private final StateTransitionTable transitions;

    private State currentState;

    private FiniteStateMachine(InternalBuilder builder) {
        transitions = builder.getStateTransitionTable();
        currentState = builder.getInitialState();
    }

    public void handle(Event event) {
        // TODO The challenge now is to create a data structure that makes this method efficient
        Map<Event, Transition> events = transitions.lookup(currentState);
        if (events != null) {
            Transition transition = events.get(event);
            if (transition != null) {
                transition.action().accept(event);
                currentState = transition.state();
            }
        }
    }

    private static class InternalBuilder {
        private final Map<State, Map<Event, Transition>> transitions = new HashMap<>();
        private final State initial;

        private Class<? extends State> fromType;
        private Class<? extends State> toType;

        private Event event;
        private State to;
        private State from;

        private boolean fastLookupsEnabled = false;

        private InternalBuilder(State initial) {
            transitions.put(initial, new HashMap<>());
            this.initial = initial;
        }

        private StateTransitionTable getStateTransitionTable() {
            Map<State, Map<Event, Transition>> unmodifiable = unmodifiableMap(transitions);
            if (fastLookupsEnabled) {
                // TODO Do something with fromType and toType
                return new MatrixTransitions(unmodifiable);
            } else {
                return new MappedTransitions(unmodifiable);
            }
        }

        private State getInitialState() {
            return initial;
        }
    }

    public interface Builder extends WithBuilder, BetweenBuilder {
        static FiniteStateMachine.Builder create(State initial) {
            InternalBuilder builder = new InternalBuilder(initial);

            return new Builder() {
                @Override
                public BetweenBuilder with(Class<? extends State> fromType, Class<? extends State> toType) {
                    if (!fromType.isEnum()) {
                        throw new IllegalArgumentException("State " + fromType.getName() + " must be an enum");
                    }
                    if (!toType.isEnum()) {
                        throw new IllegalArgumentException("State " + toType.getName() + " must be an enum");
                    }

                    builder.fastLookupsEnabled = true;
                    builder.fromType = fromType;
                    builder.toType = toType;

//            return new BetweenBuilderImpl(builder);
                    return betweenBuilder;
                }

                @Override
                public OnBuilder between(State from, State to) {
                    return betweenBuilder.between(from, to);
                }

                private final TerminalBuilder terminalBuilder = new TerminalBuilder() {
                    @Override
                    public FiniteStateMachine build() {
                        return new FiniteStateMachine(builder);
                    }

                    @Override
                    public OnBuilder between(State from, State to) {
                        return betweenBuilder.between(from, to);
                    }
                };

                private final ThenRunBuilder thenRunBuilder = action -> {
                    Map<Event, Transition> events = builder.transitions.computeIfAbsent(builder.from, e -> new HashMap<>());
                    events.put(builder.event, new Transition(builder.to, action));
                    return terminalBuilder;
                };

                private final OnBuilder onBuilder = event -> {
                    builder.event = event;
                    return thenRunBuilder;
                };

                private final BetweenBuilder betweenBuilder = (from, to) -> {
                    builder.from = from;
                    builder.to = to;
                    return onBuilder;
                };
            };
        }
    }

    public interface WithBuilder {
        BetweenBuilder with(Class<? extends State> fromType, Class<? extends State> toType);
    }

    public interface BetweenBuilder {
        OnBuilder between(State from, State to);
    }

    public interface OnBuilder {
        ThenRunBuilder on(Event event);
    }

    public interface ThenRunBuilder {
        TerminalBuilder thenRun(Consumer<Event> action);
    }

    public interface TerminalBuilder extends BetweenBuilder {
        FiniteStateMachine build();
    }
}
