package org.torinelli.parser;

import java.util.List;
import java.util.Map;

import org.torinelli.domain.enums.Street;

public class PreFlopHandProcessor extends AbstractStreetHandProcessor {

    private final HeaderParser headerParser;

    public PreFlopHandProcessor(HeaderParser headerParser,
                                ActionParser actionParser,
                                UncalledBetParser uncalledBetParser,
                                SnapshotBuilder snapshotBuilder) {
        super(actionParser, uncalledBetParser, snapshotBuilder);
        this.headerParser = headerParser;
    }

    @Override
    public void process(HandProcessingContext ctx, Map<String, List<String>> sections) {
        processStreet(ctx,
                sectionLines(sections, HandSectionSplitter.PREFLOP),
                Street.PREFLOP,
                false,
                line -> headerParser.parsePreFlopMeta(line, ctx.getPlayerMap()));
    }
}

