package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class RecursiveTransitionTest {
    private final AtomicInteger eventCount = new AtomicInteger();

    @Test
    public void testKnownEvent() {
        State stateA = mock(State.class, "stateA");
        Event eventA = mock(Event.class, "eventA");

        FiniteStateMachine fsm = FiniteStateMachine.Builder.withInitial(stateA)
                .between(stateA, stateA).on(eventA).thenRun(e -> eventCount.getAndIncrement())
                .build();

        // TODO The negative condition to make this work is a hack. Think that through better
        // TODO Also, multiple transitions between two state in the same direction should not be possible ... or should they?

        fsm.handle(eventA);

        assertEquals(1, eventCount.get());
    }
}
