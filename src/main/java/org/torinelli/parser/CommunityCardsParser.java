package org.torinelli.parser;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class CommunityCardsParser {

    public void updateFromLine(String line, ParsingState state) {
        Matcher marker = ParserPatterns.BOARD_GROUP_PATTERN.matcher(line);
        List<String> groups = marker.results()
                .map(MatchResult::group)
                .map(value -> value.substring(1, value.length() - 1).trim())
                .filter(value -> !value.isBlank())
                .toList();

        if (groups.isEmpty()) {
            return;
        }

        if (ParserPatterns.isBoardStreet(line)) {
            StringBuilder board = new StringBuilder();
            for (String group : groups) {
                if (board.length() > 0) {
                    board.append(' ');
                }
                board.append(group);
            }
            state.setCommunityCards(board.toString().replaceAll("\\s+", " ").trim());
        }
    }
}
