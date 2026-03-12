package org.torinelli.parser;

import java.util.HashMap;
import java.util.Map;

import org.torinelli.domain.enums.Street;

public class ParsingState {
    private Street currentStreet = Street.PREFLOP;
    private String communityCards = "";
    private long streetStartPot = 0L;
    private long runningPot = 0L;
    private boolean initialSnapshotCaptured = false;
    private final Map<String, Long> streetInvested = new HashMap<>();

    public Street getCurrentStreet() {
        return currentStreet;
    }

    public void setCurrentStreet(Street currentStreet) {
        this.currentStreet = currentStreet;
    }

    public String getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(String communityCards) {
        this.communityCards = communityCards;
    }

    public long getRunningPot() {
        return runningPot;
    }

    public long getStreetStartPot() {
        return streetStartPot;
    }

    public void setStreetStartPot(long streetStartPot) {
        this.streetStartPot = streetStartPot;
    }

    public boolean isInitialSnapshotCaptured() {
        return initialSnapshotCaptured;
    }

    public void setInitialSnapshotCaptured(boolean initialSnapshotCaptured) {
        this.initialSnapshotCaptured = initialSnapshotCaptured;
    }

    public void addToRunningPot(long amount) {
        this.runningPot += amount;
    }

    public void subtractFromRunningPot(long amount) {
        this.runningPot -= amount;
    }

    public Map<String, Long> getStreetInvested() {
        return streetInvested;
    }

    public void clearStreetInvested() {
        this.streetInvested.clear();
    }
}
