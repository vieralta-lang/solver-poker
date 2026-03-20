package org.torinelli.parser;

import java.util.Map;
import java.util.regex.Matcher;

import org.torinelli.domain.Player;

public class RevealedCardsParser {

    public void assignFromLine(Map<String, Player> playerMap, String line) {
        Matcher shows = ParserPatterns.SHOWS_PATTERN.matcher(line);
        if (shows.find()) {
            setPlayerCards(playerMap, shows.group(1), shows.group(2));
            return;
        }

        Matcher summary = ParserPatterns.SUMMARY_REVEALED_PATTERN.matcher(line);
        if (summary.find()) {
            setPlayerCards(playerMap, summary.group(1), summary.group(2));
        }
    }

    private void setPlayerCards(Map<String, Player> playerMap, String playerName, String cards) {
        Player player = playerMap.get(playerName);
        if (player == null) {
            return;
        }

        if (player.getHoleCards() == null || player.getHoleCards().isBlank()) {
            player.setHoleCards(cards);
            return;
        }

        if (!player.getHoleCards().equals(cards)) {
            player.setHoleCards(cards);
        }
    }
}
