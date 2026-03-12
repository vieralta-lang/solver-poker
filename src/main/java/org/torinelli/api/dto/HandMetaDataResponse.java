package org.torinelli.api.dto;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.torinelli.domain.Blind;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Seat;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HandMetaDataResponse {
    private final String handId;
    private final String tournamentId;
    private final String tableName;
    private final int maxPlayers;
    private final int buttonSeat;
    private final Blind blind;
    private final List<Seat> seats;
    private final Table table;
    private final String gameType;
    private final String format;
    private final String level;
    private final String buyIn;
    private final String rake;
    private final String ante;
    private final Map<Street, Table> snapshot;

    private HandMetaDataResponse(HandMetaData handMetaData) {
        this.handId = handMetaData.getHandId();
        this.tournamentId = handMetaData.getTournamentId();
        this.tableName = handMetaData.getTableName();
        this.maxPlayers = handMetaData.getMaxPlayers();
        this.buttonSeat = handMetaData.getButtonSeat();
        this.blind = handMetaData.getBlind();
        this.seats = handMetaData.getSeats() == null ? new ArrayList<>() : new ArrayList<>(handMetaData.getSeats());
        this.table = handMetaData.getTable();
        this.gameType = handMetaData.getGameType();
        this.format = handMetaData.getFormat();
        this.level = handMetaData.getLevel();
        this.buyIn = handMetaData.getBuyIn();
        this.rake = handMetaData.getRake();
        this.ante = handMetaData.getAnte();
        this.snapshot = new EnumMap<>(Street.class);
        if (handMetaData.getSnapshot() != null) {
            this.snapshot.putAll(handMetaData.getSnapshot());
        }
    }

    public static HandMetaDataResponse fromDomain(HandMetaData handMetaData) {
        return new HandMetaDataResponse(handMetaData);
    }

    public String getHandId() {
        return handId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public String getTableName() {
        return tableName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getButtonSeat() {
        return buttonSeat;
    }

    public Blind getBlind() {
        return blind;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public Table getTable() {
        return table;
    }

    public String getGameType() {
        return gameType;
    }

    public String getFormat() {
        return format;
    }

    public String getLevel() {
        return level;
    }

    public String getBuyIn() {
        return buyIn;
    }

    public String getRake() {
        return rake;
    }

    public String getAnte() {
        return ante;
    }

    public Map<Street, Table> getSnapshot() {
        return snapshot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HandMetaDataResponse{");
        appendIfNotBlank(sb, "handId", handId);
        appendIfNotBlank(sb, "tournamentId", tournamentId);
        appendIfNotBlank(sb, "tableName", tableName);
        sb.append("\n  maxPlayers=").append(maxPlayers);
        sb.append(",\n  buttonSeat=").append(buttonSeat);
        if (blind != null) {
            sb.append(",\n  blind=").append(blind);
        }
        appendIfNotBlank(sb, "gameType", gameType);
        appendIfNotBlank(sb, "format", format);
        appendIfNotBlank(sb, "level", level);
        appendIfNotBlank(sb, "buyIn", buyIn);
        appendIfNotBlank(sb, "rake", rake);
        appendIfNotBlank(sb, "ante", ante);
        if (!seats.isEmpty()) {
            sb.append(",\n  seatCount=").append(seats.size());
            sb.append(",\n  seats=").append(seats);
        }
        if (table != null) {
            sb.append(",\n  table=").append(table);
        }
        if (!snapshot.isEmpty()) {
            sb.append(",\n  snapshot=").append(snapshot);
        }
        sb.append("\n}");
        return sb.toString();
    }

    private void appendIfNotBlank(StringBuilder sb, String key, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(",\n  ").append(key).append("='").append(value).append('\'');
        }
    }
}
