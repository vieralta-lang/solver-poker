package org.torinelli.domain;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    
    public Hand() {
        this.cards = new ArrayList<>();
    }
    
    public Hand(Card card1, Card card2) {
        this.cards = new ArrayList<>();
        addCard(card1);
        addCard(card2);
    }
    
    public void addCard(Card card) {
        if (cards.size() < 2) {
            cards.add(card);
        } else {
            throw new IllegalStateException("Cannot have more than 2 hole cards");
        }
    }
    
    public List<Card> getCards() {
        return List.copyOf(cards);
    }
    
    public int getCardCount() {
        return cards.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}
