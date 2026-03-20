package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

import org.torinelli.domain.enums.Street;

public class ReplayTimelineFrameResponse {
    private final Street street;
    private final int step;
    private final long pot;
    private final List<String> board;
    private final List<ReplayFrameActionResponse> actions;
    private final List<ReplayPlayerUpdateResponse> playerUpdates;

    public ReplayTimelineFrameResponse(Street street,
                                       int step,
                                       long pot,
                                       List<String> board,
                                       List<ReplayFrameActionResponse> actions,
                                       List<ReplayPlayerUpdateResponse> playerUpdates) {
        this.street = street;
        this.step = step;
        this.pot = pot;
        this.board = board == null ? new ArrayList<>() : new ArrayList<>(board);
        this.actions = actions == null ? new ArrayList<>() : new ArrayList<>(actions);
        this.playerUpdates = playerUpdates == null ? new ArrayList<>() : new ArrayList<>(playerUpdates);
    }

    public Street getStreet() {
        return street;
    }

    public int getStep() {
        return step;
    }

    public long getPot() {
        return pot;
    }

    public List<String> getBoard() {
        return board;
    }

    public List<ReplayFrameActionResponse> getActions() {
        return actions;
    }

    public List<ReplayPlayerUpdateResponse> getPlayerUpdates() {
        return playerUpdates;
    }
}
