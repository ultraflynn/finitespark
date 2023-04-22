package com.mattbiggin.finitespark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ComplexEventFlowTest {
    private final AtomicInteger eventCount = new AtomicInteger();

    @Test
    public void testKnownEvent() {
        State setup = mock(State.class, "setup");
        State readyForTile = mock(State.class, "readyForTile");
        State validatingTile = mock(State.class, "validatingTile");
        State readyForMeeple = mock(State.class, "readyForMeeple");
        State validatingMeeple = mock(State.class, "validatingMeeple");
        State roundScoring = mock(State.class, "roundScoring");
        State finalScoring = mock(State.class, "finalScoring");
        Event addPlayer = mock(Event.class, "addPlayer");
        Event placeStartTile = mock(Event.class, "placeStartTile");
        Event placeTile = mock(Event.class, "placeTile");
        Event rejectTile = mock(Event.class, "rejectTile");
        Event acceptTile = mock(Event.class, "acceptTile");
        Event placeMeeple = mock(Event.class, "placeMeeple");
        Event rejectMeeple = mock(Event.class, "rejectMeeple");
        Event acceptMeeple = mock(Event.class, "acceptMeeple");
        Event declineMeeple = mock(Event.class, "declineMeeple");
        Event moreTilesAvailable = mock(Event.class, "moreTilesAvailable");
        Event tileStackEmpty = mock(Event.class, "tileStackEmpty");

        Consumer<Event> action = e -> eventCount.getAndIncrement();
        FiniteStateMachine fsm = FiniteStateMachine.Builder.create(setup)
                .between(setup, setup).on(addPlayer).thenRun(action)
                .between(setup, readyForTile).on(placeStartTile).thenRun(action)
                .between(readyForTile, validatingTile).on(placeTile).thenRun(action)
                .between(validatingTile, readyForTile).on(rejectTile).thenRun(action)
                .between(validatingTile, readyForMeeple).on(acceptTile).thenRun(action)
                .between(readyForMeeple, validatingMeeple).on(placeMeeple).thenRun(action)
                .between(validatingMeeple, readyForMeeple).on(rejectMeeple).thenRun(action)
                .between(validatingMeeple, roundScoring).on(acceptMeeple).thenRun(action)
                .between(readyForMeeple, roundScoring).on(declineMeeple).thenRun(action)
                .between(roundScoring, readyForTile).on(moreTilesAvailable).thenRun(action)
                .between(roundScoring, finalScoring).on(tileStackEmpty).thenRun(action)
                .build();

        fsm.handle(addPlayer);
        fsm.handle(addPlayer);
        fsm.handle(addPlayer);
        fsm.handle(placeStartTile);
        fsm.handle(placeTile);
        fsm.handle(rejectTile);
        fsm.handle(placeTile);
        fsm.handle(acceptTile);
        fsm.handle(placeMeeple);
        fsm.handle(rejectMeeple);
        fsm.handle(placeMeeple);
        fsm.handle(acceptMeeple);
        fsm.handle(moreTilesAvailable);
        fsm.handle(placeTile);
        fsm.handle(acceptTile);
        fsm.handle(declineMeeple);
        fsm.handle(tileStackEmpty);

        assertEquals(17, eventCount.get());
    }
}
