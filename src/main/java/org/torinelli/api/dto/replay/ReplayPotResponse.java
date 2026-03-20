package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayPotResponse {
    private final long amount;
    private final List<Integer> winnerSeats;

    public ReplayPotResponse(long amount, List<Integer> winnerSeats) {
        this.amount = amount;
        this.winnerSeats = winnerSeats == null ? new ArrayList<>() : new ArrayList<>(winnerSeats);
    }

    public long getAmount() {
        return amount;
    }

    public List<Integer> getWinnerSeats() {
        return winnerSeats;
    }
}
