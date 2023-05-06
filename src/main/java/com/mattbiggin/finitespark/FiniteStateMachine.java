package com.mattbiggin.finitespark;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableMap;

public class FiniteStateMachine {
    private final StateTransitionTable transitions;
    private final Consumer<Event> defaultAction;

    private State currentState;

    private FiniteStateMachine(InternalBuilder builder) {
        transitions = builder.getStateTransitionTable();
        currentState = builder.getInitialState();
        defaultAction = builder.defaultAction;
    }

    public void handle(Event event) {
        Map<Event, Transition> events = transitions.lookup(currentState);
        // TODO Refactor this construct
        if (events != null) {
            Transition transition = events.get(event);
            if (transition != null) {
                transition.action().accept(event);
                currentState = transition.state();
            } else {
                defaultAction.accept(event);
            }
        } else {
            defaultAction.accept(event);
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

        private Consumer<Event> defaultAction;

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

    public interface Builder extends StartVerb {
        static Builder withInitial(State initial) {
            InternalBuilder builder = new InternalBuilder(initial);

            return new Builder() {
                @Override
                public OnVerb between(State from, State to) {
                    return betweenVerb.between(from, to);
                }

                @Override
                public BetweenVerb with(Class<? extends State> fromType, Class<? extends State> toType) {
                    return withVerb.with(fromType, toType);
                }

                private final RepeatVerb repeatVerb = new RepeatVerb() {
                    @Override
                    public OnVerb between(State from, State to) {
                        return betweenVerb.between(from, to);
                    }

                    @Override
                    public FiniteStateMachine build() {
                        return buildVerb.build();
                    }

                    @Override
                    public BuildVerb orDefault(Consumer<Event> action) {
                        return orDefaultVerb.orDefault(action);
                    }
                };

                private final ThenRunVerb thenRunVerb = action -> {
                    Map<Event, Transition> events = builder.transitions.computeIfAbsent(builder.from, e -> new HashMap<>());
                    events.put(builder.event, new Transition(builder.to, action));
                    return repeatVerb;
                };
                private final OnVerb onVerb = event -> {
                    builder.event = event;
                    return thenRunVerb;
                };
                private final BetweenVerb betweenVerb = (from, to) -> {
                    builder.from = from;
                    builder.to = to;
                    return onVerb;
                };
                private final WithVerb withVerb = (fromType, toType) -> {
                    if (!fromType.isEnum()) {
                        throw new IllegalArgumentException("State " + fromType.getName() + " must be an enum");
                    }
                    if (!toType.isEnum()) {
                        throw new IllegalArgumentException("State " + toType.getName() + " must be an enum");
                    }

                    builder.fastLookupsEnabled = true;
                    builder.fromType = fromType;
                    builder.toType = toType;
                    return betweenVerb;
                };
                private final BuildVerb buildVerb = () -> new FiniteStateMachine(builder);
                private final OrDefaultVerb orDefaultVerb = action -> {
                    builder.defaultAction = action;
                    return buildVerb;
                };
            };
        }
    }

    public interface StartVerb extends WithVerb, BetweenVerb {}
    public interface RepeatVerb extends BetweenVerb, OrDefaultVerb, BuildVerb {}

    @FunctionalInterface
    public interface WithVerb {
        BetweenVerb with(Class<? extends State> fromType, Class<? extends State> toType);
    }

    @FunctionalInterface
    public interface BetweenVerb {
        OnVerb between(State from, State to);
    }

    @FunctionalInterface
    public interface OnVerb {
        ThenRunVerb on(Event event);
    }

    @FunctionalInterface
    public interface ThenRunVerb {
        RepeatVerb thenRun(Consumer<Event> action);
    }

    @FunctionalInterface
    public interface OrDefaultVerb {
        BuildVerb orDefault(Consumer<Event> action);
    }

    @FunctionalInterface
    public interface BuildVerb {
        FiniteStateMachine build();
    }
}
