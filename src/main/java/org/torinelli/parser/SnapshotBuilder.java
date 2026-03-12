package org.torinelli.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

public class SnapshotBuilder {

    public void saveSnapshot(Map<Street, Table> snapshot,
                             Street street,
                             String communityCards,
                             long potAtStreetStart,
                             long runningPot,
                             List<Player> players) {
        if (street == Street.NONE) {
            return;
        }

        Table table = new Table();
        table.setCommunityCards(communityCards);
        table.setPotAtStreetStart(potAtStreetStart);
        table.setStreetContribution(Math.max(0L, runningPot - potAtStreetStart));
        table.setTotalPot(runningPot);
        table.setPlayers(clonePlayers(players));
        snapshot.put(street, table);
    }

    public void saveInitialSnapshot(Map<Street, Table> snapshot,
                                    String communityCards,
                                    long runningPot,
                                    List<Player> players) {
        Table table = new Table();
        table.setCommunityCards(communityCards);
        table.setPotAtStreetStart(0L);
        table.setStreetContribution(runningPot);
        table.setTotalPot(runningPot);
        table.setPlayers(clonePlayers(players));
        snapshot.put(Street.NONE, table);
    }

    private List<Player> clonePlayers(List<Player> players) {
        List<Player> clone = new ArrayList<>();
        for (Player original : players) {
            Player copy = new Player(original.getName(), original.getChipStack(), original.getPosition(), original.getCards());
            copy.setSeatNumber(original.getSeatNumber());
            copy.setStatus(original.getStatus());
            if (original.getHoleCards() != null && !original.getHoleCards().isBlank()) {
                copy.setHoleCards(original.getHoleCards());
            }
            copy.setAction(original.getAction());
            clone.add(copy);
        }
        return clone;
    }
}
