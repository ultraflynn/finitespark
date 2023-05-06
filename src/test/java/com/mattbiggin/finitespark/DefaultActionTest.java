package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultActionTest {
    private final AtomicInteger unexpected = new AtomicInteger();
    private final AtomicInteger expected = new AtomicInteger();

    @Test
    public void testDefaultAction() {
        State stateA = mock(State.class, "stateA");
        State stateB = mock(State.class, "eventB");
        Event eventA = mock(Event.class, "eventA");
        Event eventB = mock(Event.class, "eventB");

        Consumer<Event> unexpectedAction = e -> unexpected.getAndIncrement();
        Consumer<Event> expectedAction = e -> expected.getAndIncrement();

        FiniteStateMachine fsm = FiniteStateMachine.Builder.withInitial(stateA)
                .between(stateA, stateB).on(eventA).thenRun(unexpectedAction)
                .orDefault(expectedAction)
                .build();

        fsm.handle(eventB);

        assertEquals(0, unexpected.get());
        assertEquals(1, expected.get());
    }
}
