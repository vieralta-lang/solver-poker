package org.torinelli.parser;

import java.util.List;
import java.util.Map;

import org.torinelli.domain.enums.Street;

public class ShowdownHandProcessor implements HandProcessor {

    private final RevealedCardsParser revealedCardsParser;
    private final SnapshotBuilder snapshotBuilder;

    public ShowdownHandProcessor(RevealedCardsParser revealedCardsParser,
                                 SnapshotBuilder snapshotBuilder) {
        this.revealedCardsParser = revealedCardsParser;
        this.snapshotBuilder = snapshotBuilder;
    }

    @Override
    public void process(HandProcessingContext ctx, Map<String, List<String>> sections) {
        List<String> lines = sections.getOrDefault(HandSectionSplitter.SHOWDOWN, List.of());
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            revealedCardsParser.assignFromLine(ctx.getPlayerMap(), line);
        }

        snapshotBuilder.saveSnapshot(ctx.getSnapshot(), Street.SHOWDOWN, ctx.getState().getCommunityCards(),
                ctx.getState().getRunningPot(), ctx.getState().getRunningPot(), ctx.getPlayers());
    }
}

