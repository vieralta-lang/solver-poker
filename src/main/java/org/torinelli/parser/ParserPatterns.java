package org.torinelli.parser;

import java.util.regex.Pattern;

public final class ParserPatterns {

    public static final Pattern HEADER_PATTERN = Pattern.compile(
            "PokerStars Hand #(\\d+): Tournament #(\\d+), .*?Level ([^ ]+) \\((\\d+)/(\\d+)\\)");

    public static final Pattern TABLE_PATTERN = Pattern.compile(
            "Table '([^']+)' ([0-9]+)-max Seat #(\\d+) is the button");

    public static final Pattern SEAT_PATTERN = Pattern.compile(
            "Seat (\\d+): (.+) \\((\\d+) in chips\\)");

    public static final Pattern ANTE_PATTERN = Pattern.compile(
            "(.+): posts the ante (\\d+)");

    public static final Pattern DEALT_TO_PATTERN = Pattern.compile(
            "Dealt to (.+) \\[(.+)]");

    public static final Pattern ACTION_PATTERN = Pattern.compile(
            "(.+): (folds|calls|bets|raises|checks)(?: (.+))?");

    public static final Pattern BLIND_PATTERN = Pattern.compile(
            "(.+): posts (small blind|big blind) (\\d+)");

    public static final Pattern SHOWS_PATTERN = Pattern.compile(
            "(.+): shows \\[(.+)]");

    public static final Pattern SUMMARY_REVEALED_PATTERN = Pattern.compile(
            "Seat \\d+: (\\S+)(?: \\([^)]*\\))? (?:showed|mucked) \\[(.+)]");

    public static final Pattern UNCALLED_BET_PATTERN = Pattern.compile(
            "Uncalled bet \\((\\d+)\\) returned to .+");

    public static final Pattern BOARD_GROUP_PATTERN = Pattern.compile("\\[([^\\]]+)]");

    private ParserPatterns() {
    }

    public static boolean isFlop(String line) {
        return line.contains("*** FLOP ***");
    }

    public static boolean isTurn(String line) {
        return line.contains("*** TURN ***");
    }

    public static boolean isRiver(String line) {
        return line.contains("*** RIVER ***");
    }

    public static boolean isShowdown(String line) {
        return line.contains("*** SHOW DOWN ***");
    }

        public static boolean isHoleCards(String line) {
                return line.contains("*** HOLE CARDS ***");
        }

    public static boolean isBoardStreet(String line) {
        return isFlop(line) || isTurn(line) || isRiver(line);
    }
}
