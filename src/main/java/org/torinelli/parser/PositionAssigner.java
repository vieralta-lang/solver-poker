package org.torinelli.parser;

import java.util.List;

import org.torinelli.domain.Player;

public class PositionAssigner {

    // missingPositions sao aqueles que nao sao BB e SB na hand.txt
    public void assignMissingPositions(List<Player> players, int buttonSeat, int maxPlayers) {
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

    private String resolvePosition(int relative, int maxPlayers) {
        if (maxPlayers != 9) {
            return "UNKNOWN";
        }

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
}
