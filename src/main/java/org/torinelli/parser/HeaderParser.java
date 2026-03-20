package org.torinelli.parser;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.torinelli.domain.Blind;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;

public class HeaderParser {

    private final PotTracker potTracker;

    public HeaderParser(PotTracker potTracker) {
        this.potTracker = potTracker;
    }

    public void parsePreFlopMeta(String line,
                              Map<String, Player> playerMap
                              ) {

        if (line.contains("Dealt to")) {
            assignCards(playerMap, line);
        }
    }

    public void parsePreDealMeta(String line,
                                 List<Player> players,
                                 Map<String, Player> playerMap,
                                 HandMetaData metaData,
                                 ParsingState state) {
        if (line.startsWith("PokerStars Hand")) {
            parseHeader(metaData, line);
        } else if (line.startsWith("Table ")) {
            parseTable(metaData, line);
        } else if (line.startsWith("Seat ")) {
            addPlayer(players, playerMap, line);
        } else if (line.contains("posts the ante")) {
            state.addToRunningPot(parseAnte(metaData, line));
        } else if (line.contains("posts small blind")) {
            state.addToRunningPot(assignBlind(playerMap, line, "SB", state));
        } else if (line.contains("posts big blind")) {
            state.addToRunningPot(assignBlind(playerMap, line, "BB", state));
        }
    }

    // parseia as infos de cada jogador individualmente, como nome, número do assento e quantidade de fichas
    private void addPlayer(List<Player> players, Map<String, Player> playerMap, String line) {
        Matcher m = ParserPatterns.SEAT_PATTERN.matcher(line);
        if (!m.find()) {
            return;
        }

        int seat = Integer.parseInt(m.group(1));
        String name = m.group(2);
        long chips = Long.parseLong(m.group(3));

        Player player = new Player(name, seat, chips);
        players.add(player);
        playerMap.put(name, player);
    }

    private void assignCards(Map<String, Player> playerMap, String line) {
        Matcher m = ParserPatterns.DEALT_TO_PATTERN.matcher(line);
        if (!m.find()) {
            return;
        }

        String name = m.group(1);
        String cards = m.group(2);
        Player player = playerMap.get(name);
        if (player != null) {
            player.setHoleCards(cards);
            player.setHero(true);
        }
    }


    //parseia as infos de cabeçalho do hand, como id, torneio, level e blinds
    private void parseHeader(HandMetaData metaData, String line) {
        Matcher m = ParserPatterns.HEADER_PATTERN.matcher(line);
        if (!m.find()) {
            return;
        }

        metaData.setHandId(m.group(1));
        metaData.setTournamentId(m.group(2));
        metaData.setLevel(m.group(3));
        int sb = Integer.parseInt(m.group(4));
        int bb = Integer.parseInt(m.group(5));
        metaData.setBlind(new Blind(sb, bb));
    }

    // parseia as infos da mesa, como nome, número máximo de jogadores e posição do botão
    private void parseTable(HandMetaData metaData, String line) {
        Matcher m = ParserPatterns.TABLE_PATTERN.matcher(line);
        if (!m.find()) {
            return;
        }

        metaData.setTableName(m.group(1));
        metaData.setMaxPlayers(Integer.parseInt(m.group(2)));
        metaData.setButtonSeat(Integer.parseInt(m.group(3)));
    }

    private long parseAnte(HandMetaData metaData, String line) {
        Matcher m = ParserPatterns.ANTE_PATTERN.matcher(line);
        if (m.find() && metaData.getAnte() == null) {
            long ante = Long.parseLong(m.group(2));
            metaData.setAnte(String.valueOf(ante));
            if (metaData.getBlind() != null) {
                metaData.getBlind().setAnte(ante);
            }
        }

        if (!m.find(0)) {
            return 0L;
        }

        return Long.parseLong(m.group(2));
    }

    private long assignBlind(Map<String, Player> playerMap, String line, String position, ParsingState state) {
        Matcher m = ParserPatterns.BLIND_PATTERN.matcher(line);
        if (!m.find()) {
            return 0L;
        }

        String playerName = m.group(1);
        Player player = playerMap.get(playerName);
        if (player != null) {
            player.setPosition(position);
            player.setStatus(position.toLowerCase());
        }

        long amount = Long.parseLong(m.group(3));
        potTracker.registerBlind(playerName, amount, state);
        return amount;
    }
}
