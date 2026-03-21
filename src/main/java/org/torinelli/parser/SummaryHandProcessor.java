package org.torinelli.parser;

import java.util.List;
import java.util.Map;

public class SummaryHandProcessor implements HandProcessor {

    private final RevealedCardsParser revealedCardsParser;

    public SummaryHandProcessor(RevealedCardsParser revealedCardsParser) {
        this.revealedCardsParser = revealedCardsParser;
    }

    @Override
    public void process(HandProcessingContext ctx, Map<String, List<String>> sections) {
        List<String> lines = sections.getOrDefault(HandSectionSplitter.SUMMARY, List.of());
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            revealedCardsParser.assignFromLine(ctx.getPlayerMap(), line);
        }
    }
}

