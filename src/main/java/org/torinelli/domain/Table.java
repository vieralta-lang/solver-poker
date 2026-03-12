package org.torinelli.domain;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private String communityCards;

    private long potAtStreetStart;

    private long streetContribution;

    private long totalPot;

    private List<Player> players;

    public Table() {
        this.players = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
    }

    public String getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(String communityCards) {
        this.communityCards = communityCards;
    }

    public long getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(long totalPot) {
        this.totalPot = totalPot;
    }

    public long getPotAtStreetStart() {
        return potAtStreetStart;
    }

    public void setPotAtStreetStart(long potAtStreetStart) {
        this.potAtStreetStart = potAtStreetStart;
    }

    public long getStreetContribution() {
        return streetContribution;
    }

    public void setStreetContribution(long streetContribution) {
        this.streetContribution = streetContribution;
    }

    @Override
    public String toString() {
        return "Table{" +
                "\n  communityCards='" + communityCards + '\'' +
            "\n  potAtStreetStart=" + potAtStreetStart +
            "\n  streetContribution=" + streetContribution +
                "\n  totalPot=" + totalPot +
                "\n  players=" + players +
                "\n}";
    }
}
