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
                .transition(addPlayer, setup, setup, action)
                .transition(placeStartTile, setup, readyForTile, action)
                .transition(placeTile, readyForTile, validatingTile, action)
                .transition(rejectTile, validatingTile, readyForTile, action)
                .transition(acceptTile, validatingTile, readyForMeeple, action)
                .transition(placeMeeple, readyForMeeple, validatingMeeple, action)
                .transition(rejectMeeple, validatingMeeple, readyForMeeple, action)
                .transition(acceptMeeple, validatingMeeple, roundScoring, action)
                .transition(declineMeeple, readyForMeeple, roundScoring, action)
                .transition(moreTilesAvailable, roundScoring, readyForTile, action)
                .transition(tileStackEmpty, roundScoring, finalScoring, action)
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
