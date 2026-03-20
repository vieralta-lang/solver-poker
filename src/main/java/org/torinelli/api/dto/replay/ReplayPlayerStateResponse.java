package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayPlayerStateResponse {
    private final int seat;
    private final String name;
    private final long stack;
    private final List<String> cards;
    private final String positionLabel;
    private final String status;
    private final boolean folded;
    private final long tableBet;
    private final String actionLabel;

    public ReplayPlayerStateResponse(int seat,
                                     String name,
                                     long stack,
                                     List<String> cards,
                                     String positionLabel,
                                     String status,
                                     boolean folded,
                                     long tableBet,
                                     String actionLabel) {
        this.seat = seat;
        this.name = name;
        this.stack = stack;
        this.cards = cards == null ? new ArrayList<>() : new ArrayList<>(cards);
        this.positionLabel = positionLabel;
        this.status = status;
        this.folded = folded;
        this.tableBet = tableBet;
        this.actionLabel = actionLabel;
    }

    public int getSeat() {
        return seat;
    }

    public String getName() {
        return name;
    }

    public long getStack() {
        return stack;
    }

    public List<String> getCards() {
        return cards;
    }

    public String getPositionLabel() {
        return positionLabel;
    }

    public String getStatus() {
        return status;
    }

    public boolean isFolded() {
        return folded;
    }

    public long getTableBet() {
        return tableBet;
    }

    public String getActionLabel() {
        return actionLabel;
    }
}
