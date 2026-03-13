package org.torinelli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

class ParserCommandTest {

    private static final String HAND = """
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

    private static final String HAND_WITHOUT_SHOWDOWN_MARKER = """
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

    @Test
    void shouldParseMetadataAndPotProgressionByStreet() {
        ParserCommand parser = new ParserCommand();

        HandMetaData result = parser.execute(HAND);

        assertEquals("260055683764", result.getHandId());
        assertEquals("3982603844", result.getTournamentId());
        assertEquals("3982603844 1", result.getTableName());
        assertEquals(9, result.getMaxPlayers());
        assertEquals(6, result.getButtonSeat());
        assertEquals("II", result.getLevel());
        assertEquals("4", result.getAnte());

        Map<Street, Table> snapshot = result.getSnapshot();
        assertNotNull(snapshot.get(Street.NONE));
        assertNotNull(snapshot.get(Street.PREFLOP));
        assertNotNull(snapshot.get(Street.FLOP));
        assertNotNull(snapshot.get(Street.TURN));
        assertNotNull(snapshot.get(Street.RIVER));
        assertNotNull(snapshot.get(Street.SHOWDOWN));

        Table none = snapshot.get(Street.NONE);
        assertEquals(0L, none.getPotAtStreetStart());
        assertEquals(81L, none.getStreetContribution());
        assertEquals(81L, none.getTotalPot());

        Table preflop = snapshot.get(Street.PREFLOP);
        assertEquals(81L, preflop.getPotAtStreetStart());
        assertEquals(120L, preflop.getStreetContribution());
        assertEquals(201L, preflop.getTotalPot());

        Table flop = snapshot.get(Street.FLOP);
        assertEquals("5s 3s Th", flop.getCommunityCards());
        assertEquals(201L, flop.getPotAtStreetStart());
        assertEquals(0L, flop.getStreetContribution());
        assertEquals(201L, flop.getTotalPot());

        Table turn = snapshot.get(Street.TURN);
        assertEquals("5s 3s Th 5c", turn.getCommunityCards());
        assertEquals(201L, turn.getTotalPot());

        Table river = snapshot.get(Street.RIVER);
        assertEquals("5s 3s Th 5c 9c", river.getCommunityCards());
        assertEquals(201L, river.getTotalPot());

        assertEquals("5s 3s Th 5c 9c", result.getTable().getCommunityCards());
        assertEquals(201L, result.getTable().getTotalPot());
    }

    @Test
    void shouldCaptureHeroAndRevealedCardsAndActions() {
        ParserCommand parser = new ParserCommand();

        HandMetaData result = parser.execute(HAND);

        Player hero = findPlayer(result.getPlayers(), "BerserkGutts");
        assertNotNull(hero);
        assertEquals("7s Ks", hero.getHoleCards());
        assertEquals("HJ", hero.getPosition());
        assertEquals("calls 30", hero.getAction().get(Street.PREFLOP));
        assertEquals("checks", hero.getAction().get(Street.FLOP));
        assertEquals("checks", hero.getAction().get(Street.TURN));
        assertEquals("checks", hero.getAction().get(Street.RIVER));

        Player fulano = findPlayer(result.getPlayers(), "Fulano");
        assertNotNull(fulano);
        assertEquals("7c 8c", fulano.getHoleCards());

        Player winner = findPlayer(result.getPlayers(), "sploosh21");
        assertNotNull(winner);
        assertEquals("9h 4d", winner.getHoleCards());
        assertEquals("bb", winner.getStatus());

        assertTrue(result.getPlayers().stream().allMatch(p -> p.getPosition() != null && !p.getPosition().isBlank()));
    }

    @Test
    void shouldComputePotWithRaisesAndUncalledBetByStreet() {
        ParserCommand parser = new ParserCommand();

        HandMetaData result = parser.execute(HAND_WITH_RERAISE_AND_UNCALLED);

        Map<Street, Table> snapshot = result.getSnapshot();
        assertNotNull(snapshot.get(Street.NONE));
        assertNotNull(snapshot.get(Street.PREFLOP));
        assertNotNull(snapshot.get(Street.FLOP));
        assertNotNull(snapshot.get(Street.TURN));

        Table none = snapshot.get(Street.NONE);
        assertEquals(0L, none.getPotAtStreetStart());
        assertEquals(30L, none.getStreetContribution());
        assertEquals(30L, none.getTotalPot());

        Table preflop = snapshot.get(Street.PREFLOP);
        assertEquals(30L, preflop.getPotAtStreetStart());
        assertEquals(130L, preflop.getStreetContribution());
        assertEquals(160L, preflop.getTotalPot());

        Table flop = snapshot.get(Street.FLOP);
        assertEquals("2c 7d Ts", flop.getCommunityCards());
        assertEquals(160L, flop.getPotAtStreetStart());
        assertEquals(300L, flop.getStreetContribution());
        assertEquals(460L, flop.getTotalPot());

        Table turn = snapshot.get(Street.TURN);
        assertEquals("2c 7d Ts 9h", turn.getCommunityCards());
        assertEquals(460L, turn.getPotAtStreetStart());
        assertEquals(0L, turn.getStreetContribution());
        assertEquals(460L, turn.getTotalPot());

        assertEquals(460L, result.getTable().getTotalPot());

        Player alpha = findPlayer(result.getPlayers(), "Alpha");
        assertNotNull(alpha);
        assertEquals("As Ah", alpha.getHoleCards());
        assertEquals("raises 20 to 40 / calls 40", alpha.getAction().get(Street.PREFLOP));
        assertEquals("raises 100 to 150", alpha.getAction().get(Street.FLOP));
        assertEquals("bets 120", alpha.getAction().get(Street.TURN));

        Player bravo = findPlayer(result.getPlayers(), "Bravo");
        assertNotNull(bravo);
        assertEquals("raises 40 to 80", bravo.getAction().get(Street.PREFLOP));
        assertEquals("bets 50 / calls 100", bravo.getAction().get(Street.FLOP));
        assertEquals("checks / folds", bravo.getAction().get(Street.TURN));
    }

    @Test
    void shouldEndOnLastDetectedStreetWhenNoShowdownMarker() {
        ParserCommand parser = new ParserCommand();

        HandMetaData result = parser.execute(HAND_WITHOUT_SHOWDOWN_MARKER);

        Map<Street, Table> snapshot = result.getSnapshot();
        assertNotNull(snapshot.get(Street.NONE));
        assertNotNull(snapshot.get(Street.PREFLOP));
        assertNotNull(snapshot.get(Street.FLOP));
        assertNull(snapshot.get(Street.SHOWDOWN));

        assertEquals("Kh 7c 2s", result.getTable().getCommunityCards());
        assertEquals(80L, result.getTable().getTotalPot());

        Table preflop = snapshot.get(Street.PREFLOP);
        assertEquals(30L, preflop.getPotAtStreetStart());
        assertEquals(10L, preflop.getStreetContribution());
        assertEquals(40L, preflop.getTotalPot());

        Table flop = snapshot.get(Street.FLOP);
        assertEquals(40L, flop.getPotAtStreetStart());
        assertEquals(40L, flop.getStreetContribution());
        assertEquals(80L, flop.getTotalPot());

        Player hero = findPlayer(result.getPlayers(), "Hero");
        assertNotNull(hero);
        assertEquals("Kd Qd", hero.getHoleCards());
        assertEquals("calls 10", hero.getAction().get(Street.PREFLOP));
        assertEquals("bets 40", hero.getAction().get(Street.FLOP));
    }

    private Player findPlayer(java.util.List<Player> players, String name) {
        return players.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }
}
