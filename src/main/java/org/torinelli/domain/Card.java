package org.torinelli.domain;

public class Card {
    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }
    
    public enum Suit {
        HEARTS('h'), DIAMONDS('d'), CLUBS('c'), SPADES('s');
        
        private final char symbol;
        
        Suit(char symbol) {
            this.symbol = symbol;
        }
        
        public char getSymbol() {
            return symbol;
        }
    }
    
    private Rank rank;
    private Suit suit;
    
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public static Card fromString(String cardStr) {
        if (cardStr == null || cardStr.length() < 2) {
            throw new IllegalArgumentException("Invalid card string: " + cardStr);
        }
        
        char rankChar = cardStr.charAt(0);
        char suitChar = cardStr.charAt(1);
        
        Rank rank = getRankFromChar(rankChar);
        Suit suit = getSuitFromChar(suitChar);
        
        return new Card(rank, suit);
    }
    
    private static Rank getRankFromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case '2' -> Rank.TWO;
            case '3' -> Rank.THREE;
            case '4' -> Rank.FOUR;
            case '5' -> Rank.FIVE;
            case '6' -> Rank.SIX;
            case '7' -> Rank.SEVEN;
            case '8' -> Rank.EIGHT;
            case '9' -> Rank.NINE;
            case 'T' -> Rank.TEN;
            case 'J' -> Rank.JACK;
            case 'Q' -> Rank.QUEEN;
            case 'K' -> Rank.KING;
            case 'A' -> Rank.ACE;
            default -> throw new IllegalArgumentException("Invalid rank: " + c);
        };
    }
    
    private static Suit getSuitFromChar(char c) {
        return switch (Character.toLowerCase(c)) {
            case 'h' -> Suit.HEARTS;
            case 'd' -> Suit.DIAMONDS;
            case 'c' -> Suit.CLUBS;
            case 's' -> Suit.SPADES;
            default -> throw new IllegalArgumentException("Invalid suit: " + c);
        };
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    @Override
    public String toString() {
        return getRankSymbol() + suit.symbol;
    }
    
    private String getRankSymbol() {
        return switch (rank) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "T";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }
}
