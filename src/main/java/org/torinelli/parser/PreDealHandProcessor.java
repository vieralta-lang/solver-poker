package org.torinelli.parser;

import java.util.List;
import java.util.Map;

public class PreDealHandProcessor implements HandProcessor {

    private final HeaderParser headerParser;
    private final PositionAssigner positionAssigner;
    private final SnapshotBuilder snapshotBuilder;

    public PreDealHandProcessor(HeaderParser headerParser,
                                PositionAssigner positionAssigner,
                                SnapshotBuilder snapshotBuilder) {
        this.headerParser = headerParser;
        this.positionAssigner = positionAssigner;
        this.snapshotBuilder = snapshotBuilder;
    }

    @Override
    public void process(HandProcessingContext ctx, Map<String, List<String>> sections) {
        List<String> lines = sections.getOrDefault(HandSectionSplitter.PRE_DEAL, List.of());
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            headerParser.parsePreDealMeta(line, ctx.getPlayers(), ctx.getPlayerMap(), ctx.getMetaData(), ctx.getState());
        }

        positionAssigner.assignMissingPositions(ctx.getPlayers(),
                ctx.getMetaData().getButtonSeat(),
                ctx.getMetaData().getMaxPlayers());
        snapshotBuilder.savePreDealSnapshot(ctx.getSnapshot(), ctx.getPlayers());
    }
}

