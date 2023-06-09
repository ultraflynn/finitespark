package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MultipleTransitionTest {
    private final AtomicInteger eventCount = new AtomicInteger();

    @Test
    public void testMultipleTransitions() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "stateB");
        State stateC = mock(State.class, "stateC");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.withInitial(stateA)
                .between(stateA, stateB).on(eventA).thenRun(e -> eventCount.getAndIncrement())
                .between(stateB, stateC).on(eventB).thenRun(e -> eventCount.getAndIncrement())
                .build();

        fsm.handle(eventA);
        fsm.handle(eventB);

        assertEquals(2, eventCount.get());
    }
}
