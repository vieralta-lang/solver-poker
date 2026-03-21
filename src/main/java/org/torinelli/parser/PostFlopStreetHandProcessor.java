package org.torinelli.parser;

import java.util.List;
import java.util.Map;

import org.torinelli.domain.enums.Street;

public class PostFlopStreetHandProcessor extends AbstractStreetHandProcessor {

    private final Street street;
    private final String sectionKey;
    private final CommunityCardsParser communityCardsParser;

    public PostFlopStreetHandProcessor(Street street,
                                       String sectionKey,
                                       CommunityCardsParser communityCardsParser,
                                       ActionParser actionParser,
                                       UncalledBetParser uncalledBetParser,
                                       SnapshotBuilder snapshotBuilder) {
        super(actionParser, uncalledBetParser, snapshotBuilder);
        this.street = street;
        this.sectionKey = sectionKey;
        this.communityCardsParser = communityCardsParser;
    }

    @Override
    public void process(HandProcessingContext ctx, Map<String, List<String>> sections) {
        processStreet(ctx,
                sectionLines(sections, sectionKey),
                street,
                true,
                line -> communityCardsParser.updateFromLine(line, ctx.getState()));
    }
}

