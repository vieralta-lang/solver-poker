package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayResponse {
    private final List<ReplayPlayerBaseResponse> players;
    private final ReplayResultResponse result;
    private final List<ReplayTimelineFrameResponse> timeline;

    public ReplayResponse(List<ReplayPlayerBaseResponse> players,
                          ReplayResultResponse result,
                          List<ReplayTimelineFrameResponse> timeline) {
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
        this.result = result;
        this.timeline = timeline == null ? new ArrayList<>() : new ArrayList<>(timeline);
    }

    public List<ReplayPlayerBaseResponse> getPlayers() {
        return players;
    }

    public ReplayResultResponse getResult() {
        return result;
    }

    public List<ReplayTimelineFrameResponse> getTimeline() {
        return timeline;
    }
}
