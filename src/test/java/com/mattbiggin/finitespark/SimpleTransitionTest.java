package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

/*
 * The simplest possible test is the transition from state A to state B, caused by an appropriate
 * event, and the execution of the associated action.
 */
public class SimpleTransitionTest {
    private final AtomicReference<Event> actual = new AtomicReference<>();

    @Test
    public void testKnownEvent() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "eventB");
        Event eventA = mock(Event.class, "eventA");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .between(stateA, stateB).on(eventA).thenRun(actual::set)
                .build();

        fsm.handle(eventA);

        assertEquals(eventA, actual.get());
    }

    @Test
    public void testUnknownEvent() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "stateB");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .between(stateA, stateB).on(eventA).thenRun(actual::set)
                .build();

        fsm.handle(eventB);

        assertNull(actual.get());
    }
}
