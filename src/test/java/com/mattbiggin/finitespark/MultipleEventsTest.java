package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MultipleEventsTest {
    private final AtomicReference<Event> actual = new AtomicReference<>();

    @Test
    public void testTwoEventsBetweenSameStates() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "eventB");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .between(stateA, stateB).on(eventA).thenRun(actual::set)
                .between(stateA, stateB).on(eventB).thenRun(actual::set)
                .build();

        fsm.handle(eventA);
        fsm.handle(eventB);

        assertEquals(eventA, actual.get());
    }
}
