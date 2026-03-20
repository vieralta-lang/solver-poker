package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayResultResponse {
    private final List<ReplayWinnerResponse> winners;
    private final List<ReplayPotResponse> pots;

    public ReplayResultResponse(List<ReplayWinnerResponse> winners, List<ReplayPotResponse> pots) {
        this.winners = winners == null ? new ArrayList<>() : new ArrayList<>(winners);
        this.pots = pots == null ? new ArrayList<>() : new ArrayList<>(pots);
    }

    public List<ReplayWinnerResponse> getWinners() {
        return winners;
    }

    public List<ReplayPotResponse> getPots() {
        return pots;
    }
}
