package org.torinelli.domain.enums;

public enum Street {

    PREFLOP("pre-flop"),
    FLOP("flop"),
    TURN("turn"),
    RIVER("river"),
    NONE("none"),
    SHOWDOWN("showdown");

    private final String name;

    Street(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
