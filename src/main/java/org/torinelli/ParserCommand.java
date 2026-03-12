package org.torinelli;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;
import org.torinelli.parser.HeaderParser;
import org.torinelli.parser.ParserPatterns;
import org.torinelli.parser.ParsingState;
import org.torinelli.parser.PotTracker;
import org.torinelli.parser.SnapshotBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParserCommand {

    private static final Logger logger = LoggerFactory.getLogger(ParserCommand.class);

    private final PotTracker potTracker = new PotTracker();

    private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();

        private final HeaderParser headerParser = new HeaderParser(potTracker);

    public HandMetaData execute(String hand) {
        HandMetaData metaData = new HandMetaData();
        List<Player> players = new ArrayList<>();
        Map<String, Player> playerMap = new HashMap<>();
        Map<Street, Table> snapshot = new EnumMap<>(Street.class);
        ParsingState state = new ParsingState();

        String[] lines = sanitizeHand(hand).split("\n");

        runStreets(lines, players, playerMap, metaData, snapshot, state);

        metaData.setPlayers(players);
        calculatePositions(metaData);
        metaData.setSnapshot(snapshot);
        metaData.setTable(snapshot.get(state.getCurrentStreet()));
        return metaData;
    }

    private void runStreets(String[] lines,
                            List<Player> players,
                            Map<String, Player> playerMap,
                            HandMetaData metaData,
                            Map<Street, Table> snapshot,
                            ParsingState state) {
        for (String line : lines) {
            if (!state.isInitialSnapshotCaptured() && ParserPatterns.isHoleCards(line)) {
                ensurePositionsAssigned(players, metaData);
                snapshotBuilder.saveInitialSnapshot(snapshot, state.getCommunityCards(), state.getRunningPot(), players);
                state.setInitialSnapshotCaptured(true);
                state.setStreetStartPot(state.getRunningPot());
            }

            Street detectedStreet = detectStreet(line, state.getCurrentStreet());
            if (detectedStreet != state.getCurrentStreet()) {
                ensurePositionsAssigned(players, metaData);
                snapshotBuilder.saveSnapshot(
                        snapshot,
                        state.getCurrentStreet(),
                        state.getCommunityCards(),
                        state.getStreetStartPot(),
                        state.getRunningPot(),
                        players
                );
                state.setCurrentStreet(detectedStreet);
                state.clearStreetInvested();
                state.setStreetStartPot(state.getRunningPot());
            }

            updateCommunityCards(line, state);
            assignRevealedCards(playerMap, line);
            state.subtractFromRunningPot(parseUncalledBet(line));

            if (state.getCurrentStreet() == Street.PREFLOP) {
                headerParser.parsePreflopMeta(line, players, playerMap, metaData, state);
            }

            parseNextStreets(playerMap, line, state.getCurrentStreet(), state);
        }

        ensurePositionsAssigned(players, metaData);
        snapshotBuilder.saveSnapshot(
            snapshot,
            state.getCurrentStreet(),
            state.getCommunityCards(),
            state.getStreetStartPot(),
            state.getRunningPot(),
            players
        );
    }

    private Street detectStreet(String line, Street current) {
        if (ParserPatterns.isFlop(line)) return Street.FLOP;
        if (ParserPatterns.isTurn(line)) return Street.TURN;
        if (ParserPatterns.isRiver(line)) return Street.RIVER;
        if (ParserPatterns.isShowdown(line)) return Street.SHOWDOWN;
        return current;
    }

    private void parseNextStreets(Map<String, Player> playerMap, String line, Street street, ParsingState state) {
        Matcher m = ParserPatterns.ACTION_PATTERN.matcher(line);
        if (!m.find()) return;

        String playerName = m.group(1);
        String action = m.group(2);
        String details = m.group(3);
        Player player = playerMap.get(playerName);

        if (player == null) {
            logger.warn("Action from unknown player: {}", playerName);
            return;
        }

        String value = action + (details != null ? " " + details : "");
        player.addAction(street, value);
        long contribution = potTracker.calculateActionContribution(playerName, action, details, state);
        if (contribution > 0) {
            state.addToRunningPot(contribution);
        }
    }


    private void calculatePositions(HandMetaData metaData) {
        int buttonSeat = metaData.getButtonSeat();
        int maxPlayers = metaData.getMaxPlayers();
        if (buttonSeat == 0 || maxPlayers == 0) return;

        for (Player player : metaData.getPlayers()) {
            if (player.getPosition() != null) continue;

            int relative = (player.getSeatNumber() - buttonSeat + maxPlayers) % maxPlayers;
            player.setPosition(resolvePosition(relative, maxPlayers));
        }
    }

    private String resolvePosition(int relative, int maxPlayers) {
        if (maxPlayers != 9) return "UNKNOWN";
        return switch (relative) {
            case 0 -> "BTN";
            case 1 -> "SB";
            case 2 -> "BB";
            case 3 -> "UTG";
            case 4 -> "UTG+1";
            case 5 -> "UTG+2";
            case 6 -> "MP";
            case 7 -> "HJ";
            case 8 -> "CO";
            default -> "UNKNOWN";
        };
    }

    private String sanitizeHand(String hand) {
        return hand.trim();
    }

    private void updateCommunityCards(String line, ParsingState state) {
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

    private void assignRevealedCards(Map<String, Player> playerMap, String line) {
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

    private long parseUncalledBet(String line) {
        Matcher m = ParserPatterns.UNCALLED_BET_PATTERN.matcher(line);
        if (!m.find()) {
            return 0L;
        }

        return Long.parseLong(m.group(1));
    }

    private void ensurePositionsAssigned(List<Player> players, HandMetaData metaData) {
        int buttonSeat = metaData.getButtonSeat();
        int maxPlayers = metaData.getMaxPlayers();
        if (buttonSeat == 0 || maxPlayers == 0) {
            return;
        }

        for (Player player : players) {
            if (player.getPosition() != null) {
                continue;
            }
            int relative = (player.getSeatNumber() - buttonSeat + maxPlayers) % maxPlayers;
            player.setPosition(resolvePosition(relative, maxPlayers));
        }
    }
}