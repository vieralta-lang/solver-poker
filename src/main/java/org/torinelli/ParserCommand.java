package org.torinelli;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;
import org.torinelli.parser.ActionParser;
import org.torinelli.parser.CommunityCardsParser;
import org.torinelli.parser.HandSectionSplitter;
import org.torinelli.parser.HeaderParser;
import org.torinelli.parser.ParsingState;
import org.torinelli.parser.PositionAssigner;
import org.torinelli.parser.PotTracker;
import org.torinelli.parser.RevealedCardsParser;
import org.torinelli.parser.SnapshotBuilder;
import org.torinelli.parser.UncalledBetParser;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParserCommand {

    private final PotTracker potTracker = new PotTracker();
    private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
    private final HeaderParser headerParser = new HeaderParser(potTracker);
    private final CommunityCardsParser communityCardsParser = new CommunityCardsParser();
    private final RevealedCardsParser revealedCardsParser = new RevealedCardsParser();
    private final PositionAssigner positionAssigner = new PositionAssigner();
    private final ActionParser actionParser = new ActionParser(potTracker);
    private final UncalledBetParser uncalledBetParser = new UncalledBetParser();

    public HandMetaData execute(String hand) {
        var ctx = new HandContext(); // instancia variaveis de contexto
        var sections = HandSectionSplitter.split(hand.trim()); // divide o hand em seções com base nos marcadores

        processPreDeal(ctx, sections.get(HandSectionSplitter.PRE_DEAL));

        //streetstartpot é o pot da street, runningPot é o pot acumulado até o momento,
        // streetContribution é o valor investido na street atual (runningPot - streetStartPot)
        processPreFlop(ctx, sections.get(HandSectionSplitter.PREFLOP));

        processBettingStreet(ctx, Street.FLOP, sections.get(HandSectionSplitter.FLOP));

        processBettingStreet(ctx, Street.TURN, sections.get(HandSectionSplitter.TURN));

        processBettingStreet(ctx, Street.RIVER, sections.get(HandSectionSplitter.RIVER));

        processShowdown(ctx, sections.get(HandSectionSplitter.SHOWDOWN));

        processSummary(ctx, sections.get(HandSectionSplitter.SUMMARY));

        ctx.metaData.setPlayers(ctx.players);
        ctx.metaData.setSnapshot(ctx.snapshot);
        ctx.metaData.setTable(lastSnapshot(ctx.snapshot));
        return ctx.metaData;
    }

    private void processPreDeal(HandContext ctx, List<String> lines) {
        if (lines == null) return;
        for (String line : lines) {
            headerParser.parsePreDealMeta(line, ctx.players, ctx.playerMap, ctx.metaData, ctx.state);
        }
        // atribui as posições faltantes (SB, BB e posições relativas) com base no número do assento do botão e no número máximo de jogadores
        positionAssigner.assignMissingPositions(ctx.players, ctx.metaData.getButtonSeat(), ctx.metaData.getMaxPlayers());

        snapshotBuilder.savePreDealSnapshot(ctx.snapshot, ctx.players);
    }

    private void processPreFlop(HandContext ctx, List<String> lines) {
        if (lines == null) return;
        ctx.state.setStreetStartPot(ctx.state.getRunningPot());
        for (String line : lines) {
            // parseia a hand do hero
            headerParser.parsePreFlopMeta(line, ctx.playerMap);

            // especificidade do pré-flop: é necessário subtrair do pote as apostas não chamadas (uncalled bets) antes de processar as ações, para garantir que o valor do pote esteja correto para o cálculo das contribuições dos jogadores
            ctx.state.subtractFromRunningPot(uncalledBetParser.parseReturnedAmount(line));
            actionParser.parseActionLine(ctx.playerMap, line, Street.PREFLOP, ctx.state);
        }
        snapshotBuilder.saveSnapshot(ctx.snapshot, Street.PREFLOP, ctx.state.getCommunityCards(),
                ctx.state.getStreetStartPot(), ctx.state.getRunningPot(), ctx.players);
    }

    private void processBettingStreet(HandContext ctx, Street street, List<String> lines) {
        if (lines == null) return;
        ctx.state.clearStreetInvested();
        ctx.state.setStreetStartPot(ctx.state.getRunningPot());
        for (String line : lines) {
            communityCardsParser.updateFromLine(line, ctx.state);
            ctx.state.subtractFromRunningPot(uncalledBetParser.parseReturnedAmount(line));
            actionParser.parseActionLine(ctx.playerMap, line, street, ctx.state);
        }
        snapshotBuilder.saveSnapshot(ctx.snapshot, street, ctx.state.getCommunityCards(),
                ctx.state.getStreetStartPot(), ctx.state.getRunningPot(), ctx.players);
    }

    private void processShowdown(HandContext ctx, List<String> lines) {
        if (lines == null) return;
        for (String line : lines) {
            revealedCardsParser.assignFromLine(ctx.playerMap, line);
        }
        snapshotBuilder.saveSnapshot(ctx.snapshot, Street.SHOWDOWN, ctx.state.getCommunityCards(),
                ctx.state.getRunningPot(), ctx.state.getRunningPot(), ctx.players);
    }

    private void processSummary(HandContext ctx, List<String> lines) {
        if (lines == null) return;
        for (String line : lines) {
            revealedCardsParser.assignFromLine(ctx.playerMap, line);
        }
    }

    private Table lastSnapshot(Map<Street, Table> snapshot) {
        Table last = null;
        for (Street street : Street.values()) {
            if (snapshot.containsKey(street)) {
                last = snapshot.get(street);
            }
        }
        return last;
    }

    private static class HandContext {
        final HandMetaData metaData = new HandMetaData();
        final List<Player> players = new ArrayList<>();
        final Map<String, Player> playerMap = new HashMap<>();
        final Map<Street, Table> snapshot = new EnumMap<>(Street.class);
        final ParsingState state = new ParsingState();
    }
}