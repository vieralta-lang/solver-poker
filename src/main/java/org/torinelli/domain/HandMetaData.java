package org.torinelli.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.torinelli.domain.enums.Street;

public class HandMetaData {
    private String handId;
    private String tournamentId;
    private String tableName;
    private int maxPlayers;
    private int buttonSeat;
    private Blind blind;
    private List<Seat> seats;
    private List<Player> players;
    private Table table;
    private String gameType;
    private String format;
    private String level;
    private String buyIn;
    private String rake;
    private String ante;
    private Map<Street, Table> snapshot;

    public HandMetaData() {
        this.seats = new ArrayList<>();
        this.players = new ArrayList<>();
        this.blind = new Blind(0, 0);
        this.snapshot = new EnumMap<>(Street.class);
    }

    public String getHandId() {
        return handId;
    }

    public void setHandId(String handId) {
        this.handId = handId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getButtonSeat() {
        return buttonSeat;
    }

    public void setButtonSeat(int buttonSeat) {
        this.buttonSeat = buttonSeat;
    }

    public Blind getBlind() {
        return blind;
    }

    public void setBlind(Blind blind) {
        this.blind = blind == null ? new Blind(0, 0) : blind;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats);
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
    }

    public Seat getSeat(int seatNumber) {
        return seats.stream()
                .filter(s -> s.getSeatNumber() == seatNumber)
                .findFirst()
                .orElse(null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBuyIn() {
        return buyIn;
    }

    public void setBuyIn(String buyIn) {
        this.buyIn = buyIn;
    }

    public String getRake() {
        return rake;
    }

    public void setRake(String rake) {
        this.rake = rake;
    }

    public String getAnte() {
        return ante;
    }

    public void setAnte(String ante) {
        this.ante = ante;
    }

    public Map<Street, Table> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Map<Street, Table> snapshot) {
        this.snapshot = new EnumMap<>(Street.class);
        if (snapshot != null) {
            this.snapshot.putAll(snapshot);
        }
    }

    @Override
    public String toString() {
        return "HandMetaData {" +
                "\n  handId: '" + handId + "'" +
                "\n  tournamentId: '" + tournamentId + "'" +
                "\n  tableName: '" + tableName + "'" +
                "\n  maxPlayers: " + maxPlayers +
                "\n  buttonSeat: " + buttonSeat +
                "\n  blind: " + blind +
                "\n  gameType: '" + gameType + "'" +
                "\n  format: '" + format + "'" +
                "\n  level: '" + level + "'" +
                "\n  buyIn: '" + buyIn + "'" +
                "\n  ante: '" + ante + "'" +
                "\n  playerCount: " + (players != null ? players.size() : 0) +
                "\n  players: " + players +
                "\n  seatCount: " + seats.size() +
                "\n  snapshot: " + snapshot +
                "\n}";
    }
}
