package org.torinelli.domain;

import org.torinelli.domain.enums.Street;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private long chipStack;
    private boolean isHero;
    private String status;
    private List<Card> cards;
    private String position;
    private Integer seatNumber;
    private String holeCards;
    private Map<Street, String> action;
    
    public Player(String name, long chipStack, String position, List<Card> cards) {
        this.name = name;
        this.chipStack = chipStack;
        this.isHero = false;
        this.status = "active";
        this.position = position;
        this.cards = cards == null ? new ArrayList<>() : new ArrayList<>(cards);
        this.action = new EnumMap<>(Street.class);
    }

    public Player(String name, long chipStack) {
        this(name, chipStack, "unknown", List.of());
    }
    
    public Player(String playerName, int seatNumber, long chips) {
        this.name = playerName;
        this.chipStack = chips;
        this.isHero = false;
        this.status = "active";
        this.seatNumber = seatNumber;
        this.position = null;
        this.cards = new ArrayList<>();
        this.action = new EnumMap<>(Street.class);
    }

    public void setHoleCards(String holeCards) {
        this.holeCards = holeCards;
        this.cards = parseHoleCards(holeCards);
    }

    public String getHoleCards() {
        return holeCards;
    }

    public Map<Street, String> getAction() {
        return action;
    }

    public void setAction(Map<Street, String> action) {
        this.action = new EnumMap<>(Street.class);
        if (action != null) {
            this.action.putAll(action);
        }
    }

    public void addAction(Street street, String value) {
        if (street == null || value == null || value.isBlank()) {
            return;
        }

        String existing = action.get(street);
        if (existing == null || existing.isBlank()) {
            action.put(street, value);
            return;
        }

        action.put(street, existing + " / " + value);
    }
    
    public String getName() {
        return name;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getChipStack() {
        return chipStack;
    }

    public boolean isHero() {
        return isHero;
    }

    public void setHero(boolean hero) {
        isHero = hero;
    }
    
    public void setChipStack(long chipStack) {
        this.chipStack = chipStack;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards == null ? new ArrayList<>() : new ArrayList<>(cards);
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Player{");
        sb.append("name='").append(name).append('\'');
        sb.append(", chipStack=").append(chipStack);
        sb.append(", isHero=").append(isHero);
        sb.append(", status='").append(status).append('\'');
        sb.append(", position='").append(position).append('\'');
        sb.append(", cards='").append(cards).append('\'');
        sb.append(", action={");

        // Append actions in street order
        boolean first = true;
        for (Street street : Street.values()) {
            String actionStr = action == null ? null : action.get(street);
            if (actionStr != null) {
                if (!first) sb.append(", ");
                sb.append(street).append(": ").append(actionStr);
                first = false;
            }
        }
        sb.append("}}");
        return sb.toString();
    }

    private List<Card> parseHoleCards(String holeCards) {
        if (holeCards == null || holeCards.isBlank()) {
            return new ArrayList<>();
        }

        String[] tokens = holeCards.trim().split("\\s+");
        if (tokens.length == 1 && tokens[0].length() == 4) {
            return List.of(
                    Card.fromString(tokens[0].substring(0, 2)),
                    Card.fromString(tokens[0].substring(2, 4))
            );
        }

        if (tokens.length == 2) {
            return List.of(Card.fromString(tokens[0]), Card.fromString(tokens[1]));
        }

        throw new IllegalArgumentException("Invalid hole cards: " + holeCards);
    }
}
