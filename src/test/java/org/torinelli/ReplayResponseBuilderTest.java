package org.torinelli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.torinelli.api.dto.HandMetaDataResponse;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.enums.Street;

class ReplayResponseBuilderTest {

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
            """;

    @Test
    void shouldBuildReplayFramesFromRawHand() {
        ParserCommand parser = new ParserCommand();
        HandMetaData metaData = parser.execute(HAND);

        HandMetaDataResponse response = HandMetaDataResponse.fromDomain(metaData, HAND);

        assertFalse(response.getPlayers().isEmpty());
        assertTrue(response.getPlayers().stream().anyMatch(p -> p.getName().equals("BerserkGutts") && p.isHero()));
        assertFalse(response.getTimeline().isEmpty());

        var finalFrame = response.getTimeline().get(response.getTimeline().size() - 1);
        long finalPot = finalFrame.getPot();
        assertEquals(201L, finalPot);
        assertEquals(Street.SHOWDOWN, finalFrame.getStreet());

        assertNotNull(response.getResult());
        assertFalse(response.getResult().getWinners().isEmpty());
        assertTrue(response.getResult().getWinners().stream().anyMatch(w -> w.getSeat() == 8));
    }
}
