package org.torinelli.parser;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

public class HandProcessingContext {

	private final HandMetaData metaData = new HandMetaData();
	private final List<Player> players = new ArrayList<>();
	private final Map<String, Player> playerMap = new HashMap<>();
	private final Map<Street, Table> snapshot = new EnumMap<>(Street.class);
	private final ParsingState state = new ParsingState();

	public HandMetaData getMetaData() {
		return metaData;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Map<String, Player> getPlayerMap() {
		return playerMap;
	}

	public Map<Street, Table> getSnapshot() {
		return snapshot;
	}

	public ParsingState getState() {
		return state;
	}

	public HandMetaData buildResult() {
		metaData.setPlayers(players);
		metaData.setSnapshot(snapshot);
		metaData.setTable(snapshot.values().stream().reduce((first, second) -> second).orElse(null));
		return metaData;
	}
}

