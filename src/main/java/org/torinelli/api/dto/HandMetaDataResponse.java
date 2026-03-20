package org.torinelli.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.torinelli.api.dto.replay.ReplayPlayerBaseResponse;
import org.torinelli.api.dto.replay.ReplayResultResponse;
import org.torinelli.api.dto.replay.ReplayResponse;
import org.torinelli.api.dto.replay.ReplayResponseBuilder;
import org.torinelli.api.dto.replay.ReplayTimelineFrameResponse;
import org.torinelli.domain.Blind;
import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Seat;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HandMetaDataResponse {
    private static final ReplayResponseBuilder REPLAY_RESPONSE_BUILDER = new ReplayResponseBuilder();

    private final String handId;
    private final String tournamentId;
    private final String tableName;
    private final int maxPlayers;
    private final int buttonSeat;
    private final Blind blind;
    private final List<Seat> seats;
    private final String gameType;
    private final String format;
    private final String level;
    private final String buyIn;
    private final String rake;
    private final List<ReplayPlayerBaseResponse> players;
    private final ReplayResultResponse result;
    private final List<ReplayTimelineFrameResponse> timeline;

    private HandMetaDataResponse(HandMetaData handMetaData, String rawHandText) {
        this.handId = handMetaData.getHandId();
        this.tournamentId = handMetaData.getTournamentId();
        this.tableName = handMetaData.getTableName();
        this.maxPlayers = handMetaData.getMaxPlayers();
        this.buttonSeat = handMetaData.getButtonSeat();
        this.blind = handMetaData.getBlind();
        this.seats = handMetaData.getSeats() == null ? new ArrayList<>() : new ArrayList<>(handMetaData.getSeats());
        this.gameType = handMetaData.getGameType();
        this.format = handMetaData.getFormat();
        this.level = handMetaData.getLevel();
        this.buyIn = handMetaData.getBuyIn();
        this.rake = handMetaData.getRake();
        ReplayResponse replay = REPLAY_RESPONSE_BUILDER.build(rawHandText, handMetaData);
        this.players = replay.getPlayers();
        this.result = replay.getResult();
        this.timeline = replay.getTimeline();
    }

    public static HandMetaDataResponse fromDomain(HandMetaData handMetaData) {
        return new HandMetaDataResponse(handMetaData, "");
    }

    public static HandMetaDataResponse fromDomain(HandMetaData handMetaData, String rawHandText) {
        return new HandMetaDataResponse(handMetaData, rawHandText);
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

    public List<ReplayPlayerBaseResponse> getPlayers() {
        return players;
    }

    public ReplayResultResponse getResult() {
        return result;
    }

    public List<ReplayTimelineFrameResponse> getTimeline() {
        return timeline;
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
        if (!seats.isEmpty()) {
            sb.append(",\n  seatCount=").append(seats.size());
            sb.append(",\n  seats=").append(seats);
        }
        if (!players.isEmpty()) {
            sb.append(",\n  players=").append(players.size());
        }
        if (!timeline.isEmpty()) {
            sb.append(",\n  timeline=").append(timeline.size());
        }
        if (result != null && !result.getWinners().isEmpty()) {
            sb.append(",\n  winners=").append(result.getWinners().size());
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
