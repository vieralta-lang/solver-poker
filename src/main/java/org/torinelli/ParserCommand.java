package org.torinelli;

import java.util.ArrayList;
import java.util.List;

import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.enums.Street;
import org.torinelli.parser.ActionParser;
import org.torinelli.parser.CommunityCardsParser;
import org.torinelli.parser.HandProcessingContext;
import org.torinelli.parser.HandProcessor;
import org.torinelli.parser.HandSectionSplitter;
import org.torinelli.parser.HeaderParser;
import org.torinelli.parser.PostFlopStreetHandProcessor;
import org.torinelli.parser.PositionAssigner;
import org.torinelli.parser.PreDealHandProcessor;
import org.torinelli.parser.PreFlopHandProcessor;
import org.torinelli.parser.PotTracker;
import org.torinelli.parser.RevealedCardsParser;
import org.torinelli.parser.ShowdownHandProcessor;
import org.torinelli.parser.SnapshotBuilder;
import org.torinelli.parser.SummaryHandProcessor;
import org.torinelli.parser.UncalledBetParser;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParserCommand {

    private final List<HandProcessor> pipeline;

    public ParserCommand() {
        var potTracker = new PotTracker();
        var snapshotBuilder = new SnapshotBuilder();
        var headerParser = new HeaderParser(potTracker);
        var communityCardsParser = new CommunityCardsParser();
        var revealedCardsParser = new RevealedCardsParser();
        var positionAssigner = new PositionAssigner();
        var actionParser = new ActionParser(potTracker);
        var uncalledBetParser = new UncalledBetParser();

        var processors = new ArrayList<HandProcessor>();
        processors.add(new PreDealHandProcessor(headerParser, positionAssigner, snapshotBuilder));
        processors.add(new PreFlopHandProcessor(headerParser, actionParser, uncalledBetParser, snapshotBuilder));

        for (Street street : List.of(Street.FLOP, Street.TURN, Street.RIVER)) {
            processors.add(new PostFlopStreetHandProcessor(street,
                    sectionKey(street),
                    communityCardsParser,
                    actionParser,
                    uncalledBetParser,
                    snapshotBuilder));
        }

        processors.add(new ShowdownHandProcessor(revealedCardsParser, snapshotBuilder));
        processors.add(new SummaryHandProcessor(revealedCardsParser));
        this.pipeline = List.copyOf(processors);
    }

    public HandMetaData execute(String hand) {
        var ctx = new HandProcessingContext();
        var sections = HandSectionSplitter.split(hand.trim()); // divide o hand em seções com base nos marcadores

        for (HandProcessor processor : pipeline) {
            processor.process(ctx, sections);
        }

        return ctx.buildResult();
    }

    private static String sectionKey(Street street) {
        return switch (street) {
            case FLOP -> HandSectionSplitter.FLOP;
            case TURN -> HandSectionSplitter.TURN;
            case RIVER -> HandSectionSplitter.RIVER;
            default -> throw new IllegalArgumentException("Unsupported post-flop street: " + street);
        };
    }
}