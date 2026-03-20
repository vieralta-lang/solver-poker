package org.torinelli.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

class HandSectionSplitterTest {

    @Test
    void shouldAlwaysContainPreDealSection() {
        String hand = "*** HOLE CARDS ***\nAlice: folds";

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertNotNull(sections.get(HandSectionSplitter.PRE_DEAL));
    }

    @Test
    void shouldAssignLinesBeforeFirstMarkerToPreDealSection() {
        String hand = """
                PokerStars Hand #123
                Table 'test' 9-max
                *** HOLE CARDS ***
                Alice: folds
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        List<String> preDeal = sections.get(HandSectionSplitter.PRE_DEAL);
        assertEquals(2, preDeal.size());
        assertTrue(preDeal.contains("PokerStars Hand #123"));
        assertTrue(preDeal.contains("Table 'test' 9-max"));
    }

    @Test
    void shouldAssignMarkerLineToItsNewSection() {
        String hand = """
                *** HOLE CARDS ***
                Alice: raises 20
                *** FLOP *** [2h 7d Ks]
                Alice: checks
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertFalse(sections.get(HandSectionSplitter.PREFLOP).contains("*** FLOP *** [2h 7d Ks]"));
        assertTrue(sections.get(HandSectionSplitter.FLOP).contains("*** FLOP *** [2h 7d Ks]"));
    }

    @Test
    void shouldAssignLinesToCorrectSectionAfterEachTransition() {
        String hand = """
                *** HOLE CARDS ***
                Alice: raises 20
                Bob: calls 20
                *** FLOP *** [2h 7d Ks]
                Alice: bets 30
                Bob: folds
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertTrue(sections.get(HandSectionSplitter.PREFLOP).contains("Alice: raises 20"));
        assertTrue(sections.get(HandSectionSplitter.PREFLOP).contains("Bob: calls 20"));
        assertTrue(sections.get(HandSectionSplitter.FLOP).contains("Alice: bets 30"));
        assertTrue(sections.get(HandSectionSplitter.FLOP).contains("Bob: folds"));
    }

    @Test
    void shouldNotCreateSectionForStreetsAbsentFromHand() {
        String hand = """
                PokerStars Hand #123
                *** HOLE CARDS ***
                Alice: folds
                *** SUMMARY ***
                Total pot 100
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertNull(sections.get(HandSectionSplitter.FLOP));
        assertNull(sections.get(HandSectionSplitter.TURN));
        assertNull(sections.get(HandSectionSplitter.RIVER));
        assertNull(sections.get(HandSectionSplitter.SHOWDOWN));
    }

    @Test
    void shouldSplitFullHandIntoAllSevenSections() {
        String hand = """
                PokerStars Hand #123
                *** HOLE CARDS ***
                Alice: calls 10
                *** FLOP *** [2h 7d Ks]
                Alice: checks
                *** TURN *** [2h 7d Ks 5c]
                Alice: checks
                *** RIVER *** [2h 7d Ks 5c 9d]
                Alice: checks
                *** SHOW DOWN ***
                Alice: shows [Ah Kh]
                *** SUMMARY ***
                Total pot 100
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertEquals(7, sections.size());
        assertNotNull(sections.get(HandSectionSplitter.PRE_DEAL));
        assertNotNull(sections.get(HandSectionSplitter.PREFLOP));
        assertNotNull(sections.get(HandSectionSplitter.FLOP));
        assertNotNull(sections.get(HandSectionSplitter.TURN));
        assertNotNull(sections.get(HandSectionSplitter.RIVER));
        assertNotNull(sections.get(HandSectionSplitter.SHOWDOWN));
        assertNotNull(sections.get(HandSectionSplitter.SUMMARY));
    }

    @Test
    void shouldPreserveInsertionOrderOfSections() {
        String hand = """
                PokerStars Hand #123
                *** HOLE CARDS ***
                Alice: calls 10
                *** FLOP *** [2h 7d Ks]
                Alice: checks
                *** TURN *** [2h 7d Ks 5c]
                Alice: checks
                *** RIVER *** [2h 7d Ks 5c 9d]
                Alice: checks
                *** SHOW DOWN ***
                Alice: shows [Ah Kh]
                *** SUMMARY ***
                Total pot 100
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        List<String> keys = new ArrayList<>(sections.keySet());
        assertEquals(HandSectionSplitter.PRE_DEAL,  keys.get(0));
        assertEquals(HandSectionSplitter.PREFLOP,   keys.get(1));
        assertEquals(HandSectionSplitter.FLOP,      keys.get(2));
        assertEquals(HandSectionSplitter.TURN,      keys.get(3));
        assertEquals(HandSectionSplitter.RIVER,     keys.get(4));
        assertEquals(HandSectionSplitter.SHOWDOWN,  keys.get(5));
        assertEquals(HandSectionSplitter.SUMMARY,   keys.get(6));
    }

    @Test
    void shouldPlaceAllLinesInPreDealWhenNoMarkersArePresent() {
        String hand = """
                PokerStars Hand #123
                Table 'test' 9-max
                Seat 1: Alice (1000 in chips)
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertEquals(1, sections.size());
        assertEquals(3, sections.get(HandSectionSplitter.PRE_DEAL).size());
    }

    @Test
    void shouldContainOnlyPreDealAndSummaryWhenHandEndedBeforeFlop() {
        String hand = """
                PokerStars Hand #123
                *** HOLE CARDS ***
                Alice: raises 100
                Bob: folds
                Alice collected 50 from pot
                *** SUMMARY ***
                Total pot 50
                """;

        LinkedHashMap<String, List<String>> sections = HandSectionSplitter.split(hand);

        assertNotNull(sections.get(HandSectionSplitter.PRE_DEAL));
        assertNotNull(sections.get(HandSectionSplitter.PREFLOP));
        assertNotNull(sections.get(HandSectionSplitter.SUMMARY));
        assertNull(sections.get(HandSectionSplitter.FLOP));
        assertNull(sections.get(HandSectionSplitter.TURN));
        assertNull(sections.get(HandSectionSplitter.RIVER));
        assertNull(sections.get(HandSectionSplitter.SHOWDOWN));
    }
}
