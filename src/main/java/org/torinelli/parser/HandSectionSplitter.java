package org.torinelli.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class HandSectionSplitter {

    public static final String PRE_DEAL = "PRE_DEAL";
    public static final String PREFLOP = "PREFLOP";
    public static final String FLOP = "FLOP";
    public static final String TURN = "TURN";
    public static final String RIVER = "RIVER";
    public static final String SHOWDOWN = "SHOWDOWN";
    public static final String SUMMARY = "SUMMARY";

    private static final List<Marker> MARKERS = List.of(
            new Marker("*** HOLE CARDS ***", PREFLOP),
            new Marker("*** FLOP ***", FLOP),
            new Marker("*** TURN ***", TURN),
            new Marker("*** RIVER ***", RIVER),
            new Marker("*** SHOW DOWN ***", SHOWDOWN),
            new Marker("*** SUMMARY ***", SUMMARY)
    );

    private HandSectionSplitter() {
    }

    public static LinkedHashMap<String, List<String>> split(String hand) {
        String[] lines = hand.split("\n");
        var sections = new LinkedHashMap<String, List<String>>();
        String current = PRE_DEAL;

        //ex: {} -> {PRE_DEAL: ["line1", "line2", ...]}
        sections.put(current, new ArrayList<>());

        // itera sobre as linhas do hand e atribui cada linha à seção correta com base nos marcadores encontrados
        for (String line : lines) {
            for (Marker marker : MARKERS) {
                if (line.contains(marker.pattern)) {
                    current = marker.section;
                    sections.putIfAbsent(current, new ArrayList<>());
                    break;
                }
            }
            sections.get(current).add(line);
        }

        return sections;
    }

    private record Marker(String pattern, String section) {
    }
}
