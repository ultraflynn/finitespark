package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DiamondTransitionTest {
    private final AtomicInteger eventCount = new AtomicInteger();

    @Test
    public void testTopRoute() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "stateB");
        State stateC = mock(State.class, "stateC");
        State stateD = mock(State.class, "stateD");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");
        Event eventC = mock(Event.class, "eventC");
        Event eventD = mock(Event.class, "eventD");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .transition(eventA, stateA, stateB, e -> eventCount.getAndIncrement())
                .transition(eventB, stateA, stateC, e -> eventCount.getAndIncrement())
                .transition(eventC, stateB, stateD, e -> eventCount.getAndIncrement())
                .transition(eventD, stateC, stateD, e -> eventCount.getAndIncrement())
                .build();

        fsm.handle(eventA);
        fsm.handle(eventC);

        assertEquals(2, eventCount.get());
    }

    @Test
    public void testBottomRoute() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "stateB");
        State stateC = mock(State.class, "stateC");
        State stateD = mock(State.class, "stateD");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");
        Event eventC = mock(Event.class, "eventC");
        Event eventD = mock(Event.class, "eventD");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .transition(eventA, stateA, stateB, e -> eventCount.getAndIncrement())
                .transition(eventB, stateA, stateC, e -> eventCount.getAndIncrement())
                .transition(eventC, stateB, stateD, e -> eventCount.getAndIncrement())
                .transition(eventD, stateC, stateD, e -> eventCount.getAndIncrement())
                .build();

        fsm.handle(eventB);
        fsm.handle(eventD);

        assertEquals(2, eventCount.get());
    }

    @Test
    public void testInvalidEventsIgnored() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "stateB");
        State stateC = mock(State.class, "stateC");
        State stateD = mock(State.class, "stateD");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");
        Event eventC = mock(Event.class, "eventC");
        Event eventD = mock(Event.class, "eventD");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(stateA)
                .transition(eventA, stateA, stateB, e -> eventCount.getAndIncrement())
                .transition(eventB, stateA, stateC, e -> eventCount.getAndIncrement())
                .transition(eventC, stateB, stateD, e -> eventCount.getAndIncrement())
                .transition(eventD, stateC, stateD, e -> eventCount.getAndIncrement())
                .build();

        fsm.handle(eventA);
        fsm.handle(eventB);
        fsm.handle(eventD);

        assertEquals(1, eventCount.get());
    }
}
