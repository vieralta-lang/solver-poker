package org.torinelli.api.dto.replay;

public class ReplayPlayerBaseResponse {
    private final int seat;
    private final String name;
    private final long stack;
    private final String position;
    private final boolean isHero;

    public ReplayPlayerBaseResponse(int seat, String name, long stack, String position, boolean isHero) {
        this.seat = seat;
        this.name = name;
        this.stack = stack;
        this.position = position;
        this.isHero = isHero;
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

    public String getPosition() {
        return position;
    }

    public boolean isHero() {
        return isHero;
    }
}
