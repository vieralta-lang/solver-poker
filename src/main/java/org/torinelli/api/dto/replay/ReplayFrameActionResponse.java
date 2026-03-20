package org.torinelli.api.dto.replay;

public class ReplayFrameActionResponse {
    private final int seat;
    private final String type;
    private final Long amount;

    public ReplayFrameActionResponse(int seat, String type, Long amount) {
        this.seat = seat;
        this.type = type;
        this.amount = amount;
    }

    public int getSeat() {
        return seat;
    }

    public String getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }
}
