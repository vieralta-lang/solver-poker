package org.torinelli.parser;

import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.torinelli.domain.Player;
import org.torinelli.domain.enums.Street;

public class ActionParser {

    private static final Logger logger = LoggerFactory.getLogger(ActionParser.class);

    private final PotTracker potTracker;

    public ActionParser(PotTracker potTracker) {
        this.potTracker = potTracker;
    }

    public void parseActionLine(Map<String, Player> playerMap, String line, Street street, ParsingState state) {
        Matcher m = ParserPatterns.ACTION_PATTERN.matcher(line);
        if (!m.find()) return;

        String playerName = m.group(1);

        // calls
        String action = m.group(2);
        // 15
        String details = m.group(3);
        Player player = playerMap.get(playerName);

        if (player == null) {
            logger.warn("Action from unknown player: {}", playerName);
            return;
        }

        // Ex: "calls 15" or "folds"
        String value = action + (details != null ? " " + details : "");
        player.addAction(street, value);

        long contribution = potTracker.calculateActionContribution(playerName, action, details, state);
        if (contribution > 0) {
            state.addToRunningPot(contribution);
        }
    }
}
