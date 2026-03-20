package org.torinelli.api.dto.replay;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplayWinnerResponse {
    private final int seat;
    private final long amount;
    private final String hand;
    private final String handDescription;

    public ReplayWinnerResponse(int seat, long amount, String hand, String handDescription) {
        this.seat = seat;
        this.amount = amount;
        this.hand = hand;
        this.handDescription = handDescription;
    }

    public int getSeat() {
        return seat;
    }

    public long getAmount() {
        return amount;
    }

    public String getHand() {
        return hand;
    }

    public String getHandDescription() {
        return handDescription;
    }
}
