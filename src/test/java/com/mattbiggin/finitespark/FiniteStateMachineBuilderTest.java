package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FiniteStateMachineBuilderTest {
    @Mock
    private State initial;

    @Mock
    private Event event;

    @Mock
    private State from;

    @Mock
    private State to;

    private enum FromType implements State {
    }

    private enum ToType implements State {
    }

    @Test
    public void testSingleTransition() {
        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(initial)
                .between(from, to).on(event).thenRun(e -> {})
                .build();
        assertNotNull(fsm);
    }

    @Test
    public void testUsingWith() {
        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(initial)
                .with(FromType.class, ToType.class)
                .between(from, to).on(event).thenRun(e -> {})
                .build();
        assertNotNull(fsm);
    }
}
