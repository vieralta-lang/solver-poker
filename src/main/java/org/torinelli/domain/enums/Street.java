package org.torinelli.domain.enums;

public enum Street {
    PRE_DEAL("pre-deal"),
    PREFLOP("pre-flop"),
    FLOP("flop"),
    TURN("turn"),
    RIVER("river"),
    SHOWDOWN("showdown");

    private final String name;

    Street(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
