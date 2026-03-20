package org.torinelli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

class ParserCommandTest {

    private static final ParserCommand parser = new ParserCommand();

    private Player findPlayer(java.util.List<Player> players, String name) {
        return players.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    @Nested
    @DisplayName("Pre-Deal Section")
    class PreDealTests {
        private static final String HAND_WITH_METADATA = """
                PokerStars Hand #260055683764: Tournament #3982603844, $4.40+$0.60 USD Hold'em No Limit - Level II (15/30) - 2026/03/11 22:11:13 ET
                Table '3982603844 1' 9-max Seat #6 is the button
                Seat 1: Fulano (1492 in chips)
                Seat 2: Kashchei19 (1355 in chips)
                Seat 3: hawaiisc (780 in chips)
                *** HOLE CARDS ***
                Dealt to Fulano [As Ah]
                Kashchei19: folds
                """;

        @Test
        void shouldParseHandMetadata() {
            HandMetaData result = parser.execute(HAND_WITH_METADATA);

            assertEquals("260055683764", result.getHandId());
            assertEquals("3982603844", result.getTournamentId());
            assertEquals("3982603844 1", result.getTableName());
            assertEquals(9, result.getMaxPlayers());
            assertEquals(6, result.getButtonSeat());
            assertEquals("II", result.getLevel());
        }

        @Test
        void shouldHaveZeroPotAtPreDeal() {
            HandMetaData result = parser.execute(HAND_WITH_METADATA);

            Table preDeal = result.getSnapshot().get(Street.PRE_DEAL);
            assertEquals(0L, preDeal.getPotAtStreetStart());
            assertEquals(0L, preDeal.getStreetContribution());
            assertEquals(0L, preDeal.getTotalPot());
        }

        @Test
        void shouldParseSeatsAndInitialChips() {
            HandMetaData result = parser.execute(HAND_WITH_METADATA);

            assertTrue(result.getPlayers().stream().anyMatch(p -> "Fulano".equals(p.getName())));
            assertTrue(result.getPlayers().stream().anyMatch(p -> "Kashchei19".equals(p.getName())));
            assertTrue(result.getPlayers().stream().anyMatch(p -> "hawaiisc".equals(p.getName())));
        }
    }

    @Nested
    @DisplayName("PreFlop Section")
    class PreFlopTests {
        private static final String PREFLOP_ONLY = """
                PokerStars Hand #123: Tournament #456, $1.00+$0.10 USD Hold'em No Limit - Level I (10/20) - 2026/03/12 10:00:00 ET
                Table 'test' 2-max Seat #1 is the button
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                Hero: posts small blind 10
                Villain: posts big blind 20
                *** HOLE CARDS ***
                Dealt to Hero [As Ah]
                Hero: raises 50 to 70
                Villain: calls 50
                *** SUMMARY ***
                Total pot 140
                """;

        @Test
        void shouldCaptureHoleCardsAndHero() {
            HandMetaData result = parser.execute(PREFLOP_ONLY);

            Player hero = findPlayer(result.getPlayers(), "Hero");
            assertNotNull(hero);
            assertEquals("As Ah", hero.getHoleCards());
        }

        @Test
        void shouldCapturePreFlopActions() {
            HandMetaData result = parser.execute(PREFLOP_ONLY);

            Player hero = findPlayer(result.getPlayers(), "Hero");
            assertEquals("raises 50 to 70", hero.getAction().get(Street.PREFLOP));
        }

        @Test
        void shouldCalculatePreFlopPot() {
            HandMetaData result = parser.execute(PREFLOP_ONLY);

            Table preFlop = result.getSnapshot().get(Street.PREFLOP);
            assertEquals(30L, preFlop.getPotAtStreetStart());
            assertEquals(120L, preFlop.getStreetContribution());
            assertEquals(140L, preFlop.getTotalPot());
        }

        @Test
        void shouldHandleRaisesInPreFlop() {
            String hand = """
                    PokerStars Hand #789: Tournament #999, $1.00+$0.10 USD Hold'em No Limit - Level I (10/20)
                    Table 'test' 2-max Seat #1 is the button
                    Seat 1: Alpha (1000 in chips)
                    Seat 2: Bravo (1000 in chips)
                    Alpha: posts small blind 10
                    Bravo: posts big blind 20
                    *** HOLE CARDS ***
                    Dealt to Alpha [Kd Qd]
                    Alpha: raises 20 to 40
                    Bravo: raises 40 to 80
                    Alpha: calls 40
                    *** SUMMARY ***
                    Total pot 160
                    """;

            HandMetaData result = parser.execute(hand);

            Player alpha = findPlayer(result.getPlayers(), "Alpha");
            assertEquals("raises 20 to 40 / calls 40", alpha.getAction().get(Street.PREFLOP));
        }
    }

    @Nested
    @DisplayName("Flop Section")
    class FlopTests {
        private static final String FLOP_HAND = """
                PokerStars Hand #111: Tournament #222, $1.00+$0.10 USD Hold'em No Limit
                Table 'test' 2-max
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                *** HOLE CARDS ***
                Dealt to Hero [Ah Kh]
                Hero: calls 10
                Villain: checks
                *** FLOP *** [2h 7d Ks]
                Villain: bets 20
                Hero: raises 40 to 60
                Villain: calls 40
                *** SUMMARY ***
                Total pot 200
                Board [2h 7d Ks]
                """;

        @Test
        void shouldCaptureFlopCommunityCards() {
            HandMetaData result = parser.execute(FLOP_HAND);

            Table flop = result.getSnapshot().get(Street.FLOP);
            assertEquals("2h 7d Ks", flop.getCommunityCards());
        }

        @Test
        void shouldCaptureFlopActions() {
            HandMetaData result = parser.execute(FLOP_HAND);

            Player hero = findPlayer(result.getPlayers(), "Hero");
            assertEquals("raises 40 to 60", hero.getAction().get(Street.FLOP));
        }

        @Test
        void shouldCalculateFlopPot() {
            HandMetaData result = parser.execute(FLOP_HAND);

            Table flop = result.getSnapshot().get(Street.FLOP);
            assertNotNull(flop);
            assertTrue(flop.getTotalPot() > 0);
        }
    }

    @Nested
    @DisplayName("Turn Section")
    class TurnTests {
        private static final String TURN_HAND = """
                PokerStars Hand #333: Tournament #444, $1.00+$0.10 USD Hold'em No Limit
                Table 'test' 2-max
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                *** HOLE CARDS ***
                Dealt to Hero [Ah Kh]
                Hero: calls 10
                Villain: checks
                *** FLOP *** [2h 7d Ks]
                Villain: checks
                Hero: bets 20
                Villain: calls 20
                *** TURN *** [2h 7d Ks 5c]
                Villain: checks
                Hero: bets 40
                Villain: folds
                *** SUMMARY ***
                Total pot 180
                Board [2h 7d Ks 5c]
                """;

        @Test
        void shouldCaptureTurnCommunityCards() {
            HandMetaData result = parser.execute(TURN_HAND);

            Table turn = result.getSnapshot().get(Street.TURN);
            assertEquals("2h 7d Ks 5c", turn.getCommunityCards());
        }

        @Test
        void shouldCaptureTurnActions() {
            HandMetaData result = parser.execute(TURN_HAND);

            Player hero = findPlayer(result.getPlayers(), "Hero");
            assertEquals("bets 40", hero.getAction().get(Street.TURN));
        }
    }

    @Nested
    @DisplayName("River Section")
    class RiverTests {
        private static final String RIVER_HAND = """
                PokerStars Hand #555: Tournament #666, $1.00+$0.10 USD Hold'em No Limit
                Table 'test' 2-max
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                *** HOLE CARDS ***
                Dealt to Hero [Ah Kh]
                Hero: calls 10
                Villain: checks
                *** FLOP *** [2h 7d Ks]
                Villain: checks
                Hero: bets 20
                Villain: calls 20
                *** TURN *** [2h 7d Ks 5c]
                Villain: checks
                Hero: bets 30
                Villain: calls 30
                *** RIVER *** [2h 7d Ks 5c 9d]
                Villain: checks
                Hero: checks
                *** SUMMARY ***
                Total pot 220
                Board [2h 7d Ks 5c 9d]
                """;

        @Test
        void shouldCaptureRiverCommunityCards() {
            HandMetaData result = parser.execute(RIVER_HAND);

            Table river = result.getSnapshot().get(Street.RIVER);
            assertEquals("2h 7d Ks 5c 9d", river.getCommunityCards());
        }

        @Test
        void shouldCaptureRiverActions() {
            HandMetaData result = parser.execute(RIVER_HAND);

            Player hero = findPlayer(result.getPlayers(), "Hero");
            assertEquals("checks", hero.getAction().get(Street.RIVER));
        }
    }

    @Nested
    @DisplayName("Showdown Section")
    class ShowdownTests {
        private static final String SHOWDOWN_HAND = """
                PokerStars Hand #777: Tournament #888, $1.00+$0.10 USD Hold'em No Limit
                Table 'test' 2-max
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                *** HOLE CARDS ***
                Dealt to Hero [Ah Kh]
                Hero: calls 10
                Villain: checks
                *** FLOP *** [2h 7d Ks]
                Villain: checks
                Hero: checks
                *** TURN *** [2h 7d Ks 5c]
                Villain: checks
                Hero: checks
                *** RIVER *** [2h 7d Ks 5c 9d]
                Villain: checks
                Hero: checks
                *** SHOW DOWN ***
                Villain: shows [Kd Kc] (three of a kind, Kings)
                Hero: mucks hand
                Villain collected 40 from pot
                *** SUMMARY ***
                Total pot 40
                Board [2h 7d Ks 5c 9d]
                """;

        @Test
        void shouldHaveShowdownSection() {
            HandMetaData result = parser.execute(SHOWDOWN_HAND);

            assertNotNull(result.getSnapshot().get(Street.SHOWDOWN));
        }

        @Test
        void shouldCaptureRevealedCards() {
            HandMetaData result = parser.execute(SHOWDOWN_HAND);

            Player villain = findPlayer(result.getPlayers(), "Villain");
            assertNotNull(villain);
            assertEquals("Kd Kc", villain.getHoleCards());
        }
    }

    @Nested
    @DisplayName("Summary Section")
    class SummaryTests {
        private static final String HAND_WITH_SUMMARY = """
                PokerStars Hand #999: Tournament #1111, $1.00+$0.10 USD Hold'em No Limit
                Table 'test' 2-max
                Seat 1: Hero (1000 in chips)
                Seat 2: Villain (1000 in chips)
                *** HOLE CARDS ***
                Dealt to Hero [Ah Kh]
                Hero: calls 10
                Villain: checks
                *** FLOP *** [2h 7d Ks]
                Villain: checks
                Hero: bets 20
                Villain: folds
                Hero collected 40 from pot
                *** SUMMARY ***
                Total pot 40 | Rake 0
                Board [2h 7d Ks]
                Seat 1: Hero collected (40)
                Seat 2: Villain folded on the Flop
                """;

        @Test
        void shouldCaptureFinalPotTotal() {
            HandMetaData result = parser.execute(HAND_WITH_SUMMARY);

            assertEquals(40L, result.getTable().getTotalPot());
        }
    }

    @Nested
    @DisplayName("Full Hand Integration Tests")
    class FullHandTests {
        private static final String COMPLETE_HAND = """
                PokerStars Hand #260055683764: Tournament #3982603844, $4.40+$0.60 USD Hold'em No Limit - Level II (15/30) - 2026/03/11 22:11:13 ET
                Table '3982603844 1' 9-max Seat #6 is the button
                Seat 1: Fulano (1492 in chips)
                Seat 2: Kashchei19 (1355 in chips)
                Seat 3: hawaiisc (780 in chips)
                Seat 4: BerserkGutts (3077 in chips)
                Seat 5: Allnuzee (1375 in chips)
                Seat 6: bruma1312 (307 in chips)
                Seat 7: hossD59 (1582 in chips)
                Seat 8: sploosh21 (1305 in chips)
                Seat 9: kkcunha (2227 in chips)
                Fulano: posts the ante 4
                Kashchei19: posts the ante 4
                hawaiisc: posts the ante 4
                BerserkGutts: posts the ante 4
                Allnuzee: posts the ante 4
                bruma1312: posts the ante 4
                hossD59: posts the ante 4
                sploosh21: posts the ante 4
                kkcunha: posts the ante 4
                hossD59: posts small blind 15
                sploosh21: posts big blind 30
                *** HOLE CARDS ***
                Dealt to BerserkGutts [7s Ks]
                kkcunha: folds
                Fulano: calls 30
                Kashchei19: folds
                hawaiisc: calls 30
                BerserkGutts: calls 30
                Allnuzee: calls 30
                bruma1312: folds
                hossD59: folds
                sploosh21: checks
                *** FLOP *** [5s 3s Th]
                sploosh21: checks
                Fulano: checks
                hawaiisc: checks
                BerserkGutts: checks
                Allnuzee: checks
                *** TURN *** [5s 3s Th] [5c]
                sploosh21: checks
                Fulano: checks
                hawaiisc: checks
                BerserkGutts: checks
                Allnuzee: checks
                *** RIVER *** [5s 3s Th 5c] [9c]
                sploosh21: checks
                Fulano: checks
                hawaiisc: checks
                BerserkGutts: checks
                Allnuzee: checks
                *** SHOW DOWN ***
                sploosh21: shows [9h 4d] (two pair, Nines and Fives)
                Fulano: mucks hand
                hawaiisc: mucks hand
                BerserkGutts: mucks hand
                Allnuzee: mucks hand
                sploosh21 collected 201 from pot
                *** SUMMARY ***
                Total pot 201 | Rake 0
                Board [5s 3s Th 5c 9c]
                Seat 1: Fulano mucked [7c 8c]
                Seat 2: Kashchei19 folded before Flop (didn't bet)
                Seat 3: hawaiisc mucked [Ad 6h]
                Seat 4: BerserkGutts mucked [7s Ks]
                Seat 5: Allnuzee mucked [Qs 2s]
                Seat 6: bruma1312 (button) folded before Flop (didn't bet)
                Seat 7: hossD59 (small blind) folded before Flop
                Seat 8: sploosh21 (big blind) showed [9h 4d] and won (201) with two pair, Nines and Fives
                Seat 9: kkcunha folded before Flop (didn't bet)
                """;

        private static final String HAND_WITH_RERAISE_AND_UNCALLED = """
                PokerStars Hand #260099999001: Tournament #3982999001, $1.00+$0.10 USD Hold'em No Limit - Level I (10/20) - 2026/03/12 10:00:00 ET
                Table '3982999001 1' 9-max Seat #3 is the button
                Seat 1: Alpha (1000 in chips)
                Seat 2: Bravo (1000 in chips)
                Seat 3: Charlie (1000 in chips)
                Alpha: posts small blind 10
                Bravo: posts big blind 20
                *** HOLE CARDS ***
                Dealt to Alpha [As Ah]
                Charlie: folds
                Alpha: raises 20 to 40
                Bravo: raises 40 to 80
                Alpha: calls 40
                *** FLOP *** [2c 7d Ts]
                Bravo: bets 50
                Alpha: raises 100 to 150
                Bravo: calls 100
                *** TURN *** [2c 7d Ts] [9h]
                Bravo: checks
                Alpha: bets 120
                Bravo: folds
                Uncalled bet (120) returned to Alpha
                Alpha collected 460 from pot
                *** SUMMARY ***
                Total pot 460 | Rake 0
                Board [2c 7d Ts 9h]
                Seat 1: Alpha collected (460)
                Seat 2: Bravo folded on the Turn
                Seat 3: Charlie folded before Flop (didn't bet)
                """;

        @Test
        void shouldParseFullMultiStreetHand() {
            HandMetaData result = parser.execute(COMPLETE_HAND);

            Map<Street, Table> snapshot = result.getSnapshot();
            assertNotNull(snapshot.get(Street.PRE_DEAL));
            assertNotNull(snapshot.get(Street.PREFLOP));
            assertNotNull(snapshot.get(Street.FLOP));
            assertNotNull(snapshot.get(Street.TURN));
            assertNotNull(snapshot.get(Street.RIVER));
            assertNotNull(snapshot.get(Street.SHOWDOWN));
        }

        @Test
        void shouldCalculatePotProgressionAcrossAllStreets() {
            HandMetaData result = parser.execute(COMPLETE_HAND);

            Table preflop = result.getSnapshot().get(Street.PREFLOP);
            Table flop = result.getSnapshot().get(Street.FLOP);
            Table turn = result.getSnapshot().get(Street.TURN);
            Table river = result.getSnapshot().get(Street.RIVER);

            assertEquals(81L, preflop.getPotAtStreetStart());
            assertEquals(201L, flop.getPotAtStreetStart());
            assertEquals(201L, turn.getPotAtStreetStart());
            assertEquals(201L, river.getPotAtStreetStart());
        }

        @Test
        void shouldCaptureCommunityCardsProgression() {
            HandMetaData result = parser.execute(COMPLETE_HAND);

            assertEquals("5s 3s Th", result.getSnapshot().get(Street.FLOP).getCommunityCards());
            assertEquals("5s 3s Th 5c", result.getSnapshot().get(Street.TURN).getCommunityCards());
            assertEquals("5s 3s Th 5c 9c", result.getSnapshot().get(Street.RIVER).getCommunityCards());
        }

        @Test
        void shouldCaptureHeroAndActionsAcrossMultipleStreets() {
            HandMetaData result = parser.execute(COMPLETE_HAND);

            Player hero = findPlayer(result.getPlayers(), "BerserkGutts");
            assertNotNull(hero);
            assertEquals("7s Ks", hero.getHoleCards());
            assertEquals("calls 30", hero.getAction().get(Street.PREFLOP));
            assertEquals("checks", hero.getAction().get(Street.FLOP));
            assertEquals("checks", hero.getAction().get(Street.TURN));
            assertEquals("checks", hero.getAction().get(Street.RIVER));
        }

        @Test
        void shouldHandleRaisesAndUncalledBetAcrossStreets() {
            HandMetaData result = parser.execute(HAND_WITH_RERAISE_AND_UNCALLED);

            Player alpha = findPlayer(result.getPlayers(), "Alpha");
            assertNotNull(alpha);
            assertEquals("As Ah", alpha.getHoleCards());
            assertEquals("raises 20 to 40 / calls 40", alpha.getAction().get(Street.PREFLOP));
            assertEquals("raises 100 to 150", alpha.getAction().get(Street.FLOP));
            assertEquals("bets 120", alpha.getAction().get(Street.TURN));

            Table flop = result.getSnapshot().get(Street.FLOP);
            assertEquals(160L, flop.getPotAtStreetStart());
            assertEquals(300L, flop.getStreetContribution());
            assertEquals(460L, flop.getTotalPot());
        }

        @Test
        void shouldEndOnLastDetectedStreetWhenNoShowdownMarker() {
            String handWithoutShowdown = """
                    PokerStars Hand #260099999002: Tournament #3982999002, $1.00+$0.10 USD Hold'em No Limit - Level I (10/20) - 2026/03/12 10:05:00 ET
                    Table '3982999002 1' 9-max Seat #2 is the button
                    Seat 1: Hero (1000 in chips)
                    Seat 2: Villain (1000 in chips)
                    Hero: posts small blind 10
                    Villain: posts big blind 20
                    *** HOLE CARDS ***
                    Dealt to Hero [Kd Qd]
                    Hero: calls 10
                    Villain: checks
                    *** FLOP *** [Kh 7c 2s]
                    Hero: bets 40
                    Villain: folds
                    Hero collected 80 from pot
                    *** SUMMARY ***
                    Total pot 80 | Rake 0
                    Board [Kh 7c 2s]
                    Seat 1: Hero collected (80)
                    Seat 2: Villain folded on the Flop
                    """;

            HandMetaData result = parser.execute(handWithoutShowdown);

            Map<Street, Table> snapshot = result.getSnapshot();
            assertNotNull(snapshot.get(Street.PRE_DEAL));
            assertNotNull(snapshot.get(Street.PREFLOP));
            assertNotNull(snapshot.get(Street.FLOP));
            assertNull(snapshot.get(Street.SHOWDOWN));
            assertNull(snapshot.get(Street.TURN));
            assertNull(snapshot.get(Street.RIVER));
        }
    }
}
