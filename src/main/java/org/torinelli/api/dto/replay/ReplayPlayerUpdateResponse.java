package org.torinelli.api.dto.replay;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplayPlayerUpdateResponse {
    private final int seat;
    private final Long stack;
    private final Boolean folded;
    private final Boolean inHand;
    private final String holeCards;

    public ReplayPlayerUpdateResponse(int seat,
                                      Long stack,
                                      Boolean folded,
                                      Boolean inHand,
                                      String holeCards) {
        this.seat = seat;
        this.stack = stack;
        this.folded = folded;
        this.inHand = inHand;
        this.holeCards = holeCards;
    }

    public int getSeat() {
        return seat;
    }

    public Long getStack() {
        return stack;
    }

    public Boolean getFolded() {
        return folded;
    }

    public Boolean getInHand() {
        return inHand;
    }

    public String getHoleCards() {
        return holeCards;
    }
}
