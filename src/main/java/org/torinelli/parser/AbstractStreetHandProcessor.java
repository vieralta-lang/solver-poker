package org.torinelli.parser;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.torinelli.domain.enums.Street;

public abstract class AbstractStreetHandProcessor implements HandProcessor {

    private final ActionParser actionParser;
    private final UncalledBetParser uncalledBetParser;
    private final SnapshotBuilder snapshotBuilder;

    protected AbstractStreetHandProcessor(ActionParser actionParser,
                                          UncalledBetParser uncalledBetParser,
                                          SnapshotBuilder snapshotBuilder) {
        this.actionParser = actionParser;
        this.uncalledBetParser = uncalledBetParser;
        this.snapshotBuilder = snapshotBuilder;
    }

    protected List<String> sectionLines(Map<String, List<String>> sections, String sectionKey) {
        return sections.getOrDefault(sectionKey, List.of());
    }

    protected void processStreet(HandProcessingContext ctx,
                                 List<String> lines,
                                 Street street,
                                 boolean clearStreetInvested,
                                 Consumer<String> preLineProcessor) {
        if (lines.isEmpty()) {
            return;
        }

        if (clearStreetInvested) {
            ctx.getState().clearStreetInvested();
        }

        ctx.getState().setStreetStartPot(ctx.getState().getRunningPot());
        for (String line : lines) {
            preLineProcessor.accept(line);
            processLine(line, street, ctx);
        }

        snapshotBuilder.saveSnapshot(ctx.getSnapshot(), street, ctx.getState().getCommunityCards(),
                ctx.getState().getStreetStartPot(), ctx.getState().getRunningPot(), ctx.getPlayers());
    }

    protected void processLine(String line, Street street, HandProcessingContext ctx) {
        ctx.getState().subtractFromRunningPot(uncalledBetParser.parseReturnedAmount(line));
        actionParser.parseActionLine(ctx.getPlayerMap(), line, street, ctx.getState());
    }
}

