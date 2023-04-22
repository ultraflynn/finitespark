package com.mattbiggin.test;

import com.mattbiggin.finitespark.Event;
import com.mattbiggin.finitespark.FiniteStateMachine;
import com.mattbiggin.finitespark.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class BuilderVisibilityTest {
    @Mock
    private State initial;

    private enum From implements State {}

    private enum To implements State {}

    @Mock
    private State from;

    @Mock
    private State to;

    @Mock
    private Event event;

    @Test
    public void testBuilderUsingWithForVisibility() {
        FiniteStateMachine.Builder.create(initial)
                .with(From.class, To.class)
                .between(from, to)
                .on(event)
                .thenRun(e -> {})
                .build();
    }

    @Test
    public void testBuilderUsingBetweenForVisibility() {
        FiniteStateMachine.Builder.create(initial)
                .between(from, to)
                .on(event)
                .thenRun(e -> {})
                .build();
    }
}
