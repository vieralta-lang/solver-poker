package org.torinelli.domain;

public class Blind {
    private long smallBlindAmount;
    private long bigBlindAmount;
    private long ante;
    
    public Blind(long smallBlindAmount, long bigBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.ante = 0;
    }
    
    public Blind(long smallBlindAmount, long bigBlindAmount, long ante) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
        this.ante = ante;
    }
    
    public long getSmallBlindAmount() {
        return smallBlindAmount;
    }
    
    public void setSmallBlindAmount(long smallBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
    }
    
    public long getBigBlindAmount() {
        return bigBlindAmount;
    }
    
    public void setBigBlindAmount(long bigBlindAmount) {
        this.bigBlindAmount = bigBlindAmount;
    }
    
    public long getAnte() {
        return ante;
    }
    
    public void setAnte(long ante) {
        this.ante = ante;
    }
    
    @Override
    public String toString() {
        return "Blind{" +
                "smallBlindAmount=" + smallBlindAmount +
                ", bigBlindAmount=" + bigBlindAmount +
                ", ante=" + ante +
                '}';
    }
}
