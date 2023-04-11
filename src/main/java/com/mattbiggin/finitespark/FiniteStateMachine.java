package com.mattbiggin.finitespark;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FiniteStateMachine {
    private final StateTransitionTable transitions;

    private State currentState;

    private FiniteStateMachine(Builder builder) {
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

    public static class Builder {
        private final Map<State, Map<Event, Transition>> transitions = new HashMap<>();
        private final State initial;

        private Class<State> fromType;
        private Class<State> toType;

        private boolean fastLookupsEnabled = false;

        private Builder(State initial) {
            transitions.put(initial, new HashMap<>());
            this.initial = initial;
        }

        public static Builder create(State initial) {
            return new Builder(initial);
        }


        /**
         * Specifying the "from" and "to" state as enums enables high-performance mode where the configuration
         * is translated into a 2 dimensional array and allow O(1) time complexity when looking up the next
         * transition.
         *
         * @param fromType the from type
         * @param toType   the to type
         * @return the builder
         */
        public Builder with(Class<State> fromType, Class<State> toType) {
            // TODO Both must implement State and be enums
            if (!fromType.isEnum()) {
                throw new IllegalArgumentException("State " + fromType.getName() + " must be an enum");
            }
            if (!toType.isEnum()) {
                throw new IllegalArgumentException("State " + toType.getName() + " must be an enum");
            }
            this.fromType = fromType;
            this.toType = toType;
            this.fastLookupsEnabled = true;
            return this;
        }

        private static final class BuilderUsingWithWrapper {
            private final Builder builder;

            private BuilderUsingWithWrapper(Builder builder) {
                this.builder = builder;
            }

            FiniteStateMachine build() {
                return new FiniteStateMachine(builder);
            }
        }

        public Builder transition(Event event, State from, State to, Consumer<Event> action) {
            Map<Event, Transition> events = transitions.computeIfAbsent(from, e -> new HashMap<>());
            events.put(event, new Transition(to, action));
            return this;
        }

        // TODO with(fromEnum, toEnum) which allows high-performance execution mode

        // TODO .between(from, to).on(event).thenRun(action)
        // TODO Each creates a new builder which only has the correct next fluent API call

        private StateTransitionTable getStateTransitionTable() {
            if (fastLookupsEnabled) {
                return new MatrixTransitions(transitions);
            } else {
                return new MappedTransitions(transitions);
            }
        }

        private State getInitialState() {
            return initial;
        }

        public FiniteStateMachine build() {
            return new FiniteStateMachine(this);
        }
    }
}