package org.torinelli.domain;

public class Seat {
    private int seatNumber;
    private Player player;
    private boolean isButton;
    private boolean isSmallBlind;
    private boolean isBigBlind;
    
    public Seat(int seatNumber, Player player) {
        this.seatNumber = seatNumber;
        this.player = player;
        this.isButton = false;
        this.isSmallBlind = false;
        this.isBigBlind = false;
    }
    
    public int getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public boolean isButton() {
        return isButton;
    }
    
    public void setButton(boolean button) {
        isButton = button;
    }
    
    public boolean isSmallBlind() {
        return isSmallBlind;
    }
    
    public void setSmallBlind(boolean smallBlind) {
        isSmallBlind = smallBlind;
    }
    
    public boolean isBigBlind() {
        return isBigBlind;
    }
    
    public void setBigBlind(boolean bigBlind) {
        isBigBlind = bigBlind;
    }
    
    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber=" + seatNumber +
                ", player=" + player +
                ", isButton=" + isButton +
                ", isSmallBlind=" + isSmallBlind +
                ", isBigBlind=" + isBigBlind +
                '}';
    }
}
