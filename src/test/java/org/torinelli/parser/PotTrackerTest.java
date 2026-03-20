package org.torinelli.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PotTrackerTest {

    private final PotTracker potTracker = new PotTracker();

    @Test
    void returnsZeroWhenDetailsAreNull() {
        ParsingState state = new ParsingState();

        long contribution = potTracker.calculateActionContribution("Hero", "calls", null, state);

        assertEquals(0L, contribution);
        assertEquals(0L, state.getStreetInvested().getOrDefault("Hero", 0L));
    }

    @Test
    void returnsZeroWhenDetailsAreBlank() {
        ParsingState state = new ParsingState();

        long contribution = potTracker.calculateActionContribution("Hero", "bets", "   ", state);

        assertEquals(0L, contribution);
        assertEquals(0L, state.getStreetInvested().getOrDefault("Hero", 0L));
    }

    @Test
    void returnsZeroForFoldAndCheckActions() {
        ParsingState state = new ParsingState();

        long foldContribution = potTracker.calculateActionContribution("Hero", "folds", "100", state);
        long checkContribution = potTracker.calculateActionContribution("Hero", "checks", "100", state);

        assertEquals(0L, foldContribution);
        assertEquals(0L, checkContribution);
        assertEquals(0L, state.getStreetInvested().getOrDefault("Hero", 0L));
    }

    @Test
    void returnsZeroWhenDetailsContainNoNumbers() {
        ParsingState state = new ParsingState();

        long contribution = potTracker.calculateActionContribution("Hero", "calls", "all-in", state);

        assertEquals(0L, contribution);
        assertEquals(0L, state.getStreetInvested().getOrDefault("Hero", 0L));
    }

    @Test
    void calculatesRaiseContributionUsingTargetAmountWhenPlayerHasNoPreviousInvestment() {
        ParsingState state = new ParsingState();

        long contribution = potTracker.calculateActionContribution("Hero", "raises", "20 to 70", state);

        assertEquals(70L, contribution);
        assertEquals(70L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void calculatesRaiseContributionAsDeltaFromAlreadyInvestedAmount() {
        ParsingState state = new ParsingState();
        potTracker.registerBlind("Hero", 10L, state);

        long contribution = potTracker.calculateActionContribution("Hero", "raises", "20 to 70", state);

        assertEquals(60L, contribution);
        assertEquals(70L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void returnsZeroForRaiseWhenTargetAmountIsNotGreaterThanAlreadyInvested() {
        ParsingState state = new ParsingState();
        potTracker.registerBlind("Hero", 80L, state);

        long contribution = potTracker.calculateActionContribution("Hero", "raises", "20 to 70", state);

        assertEquals(0L, contribution);
        assertEquals(80L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void accumulatesStreetInvestmentForCallActions() {
        ParsingState state = new ParsingState();

        long firstCall = potTracker.calculateActionContribution("Hero", "calls", "20", state);
        long secondCall = potTracker.calculateActionContribution("Hero", "calls", "30", state);

        assertEquals(20L, firstCall);
        assertEquals(30L, secondCall);
        assertEquals(50L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void accumulatesStreetInvestmentForBetActions() {
        ParsingState state = new ParsingState();

        long firstBet = potTracker.calculateActionContribution("Hero", "bets", "25", state);
        long secondBet = potTracker.calculateActionContribution("Hero", "bets", "40", state);

        assertEquals(25L, firstBet);
        assertEquals(40L, secondBet);
        assertEquals(65L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void registerBlindAccumulatesAmountForSamePlayer() {
        ParsingState state = new ParsingState();

        potTracker.registerBlind("Hero", 10L, state);
        potTracker.registerBlind("Hero", 20L, state);

        assertEquals(30L, state.getStreetInvested().get("Hero"));
    }

    @Test
    void multiplePlayersRegisterBlind() {
        ParsingState state = new ParsingState();

        potTracker.registerBlind("Hero", 10L, state);
        potTracker.registerBlind("Villain", 20L, state);
        potTracker.calculateActionContribution("Hero", "bets", "25", state);

        assertEquals(35L, state.getStreetInvested().get("Hero"));
        assertEquals(20L, state.getStreetInvested().get("Villain"));

    }
}

