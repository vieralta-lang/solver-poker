package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayFrameResponse {
    private final int step;
    private final long potAmount;
    private final List<String> boardCards;
    private final List<ReplayPlayerStateResponse> players;

    public ReplayFrameResponse(int step,
                               long potAmount,
                               List<String> boardCards,
                               List<ReplayPlayerStateResponse> players) {
        this.step = step;
        this.potAmount = potAmount;
        this.boardCards = boardCards == null ? new ArrayList<>() : new ArrayList<>(boardCards);
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
    }

    public int getStep() {
        return step;
    }

    public long getPotAmount() {
        return potAmount;
    }

    public List<String> getBoardCards() {
        return boardCards;
    }

    public List<ReplayPlayerStateResponse> getPlayers() {
        return players;
    }
}
