package org.torinelli.api.dto.replay;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.torinelli.domain.HandMetaData;
import org.torinelli.domain.Player;
import org.torinelli.domain.Table;
import org.torinelli.domain.enums.Street;

public class ReplayResponseBuilder {

    private static final Pattern ACTION_LINE_PATTERN = Pattern.compile("^([^:]+):\\s+(.+)$");
    private static final Pattern RAISE_PATTERN = Pattern.compile("(?i)^raises\\s+(.+?)\\s+to\\s+(.+)$");
    private static final Pattern UNCALLED_BET_PATTERN = Pattern.compile("(?i)^Uncalled bet \\((.+)\\) returned to (.+)$");
    private static final Pattern COLLECTED_PATTERN = Pattern.compile("(?i)^(.+?) collected (.+) from pot$");
    private static final Pattern SUMMARY_WIN_PATTERN = Pattern.compile(
            "(?i)^Seat (\\d+): (\\S+)(?: \\([^)]*\\))? (?:showed \\[(.+)] and won \\((.+)\\) with (.+)|collected \\((.+)\\))$");

    private static final List<Street> REPLAY_STREETS = List.of(
            Street.PREFLOP,
            Street.FLOP,
            Street.TURN,
            Street.RIVER,
            Street.SHOWDOWN
    );

    public ReplayResponse build(String rawHandText, HandMetaData metaData) {
        Map<Street, List<ReplayAction>> actionsByStreet = parseActionsByStreet(rawHandText);
        Map<String, Long> currentStackByPlayer = buildBaseStackByPlayer(metaData);
        Map<String, Integer> seatByPlayerName = buildSeatByPlayerName(metaData);
        Set<String> foldedPlayers = new HashSet<>();

        List<ReplayPlayerBaseResponse> players = buildBasePlayers(metaData);
        List<ReplayTimelineFrameResponse> timeline = new ArrayList<>();

        int step = 0;

        for (Street street : REPLAY_STREETS) {
            Table table = metaData.getSnapshot().get(street);
            if (table == null) {
                continue;
            }

            List<ReplayAction> streetActions = actionsByStreet.getOrDefault(street, List.of());
            List<ReplayFrameActionResponse> frameActions = new ArrayList<>();
            List<ReplayPlayerUpdateResponse> playerUpdates = new ArrayList<>();
            for (ReplayAction action : streetActions) {
                Integer seat = seatByPlayerName.get(action.getActor());
                if (seat == null) {
                    continue;
                }

                frameActions.add(new ReplayFrameActionResponse(seat, toActionType(action.getActionType()), toActionAmount(action)));

                long stackBefore = currentStackByPlayer.getOrDefault(action.getActor(), 0L);
                long nextStack = stackBefore;
                long contribution = contributionForPot(action);
                if (contribution > 0) {
                    nextStack = Math.max(0L, stackBefore - contribution);
                    currentStackByPlayer.put(action.getActor(), nextStack);
                }

                Boolean folded = null;
                Boolean inHand = null;
                if (action.getActionType() == ReplayActionType.FOLD) {
                    foldedPlayers.add(action.getActor());
                    folded = true;
                    inHand = false;
                }

                String holeCards = null;
                Player player = findPlayer(metaData.getPlayers(), action.getActor());
                if (player != null && player.getHoleCards() != null && !player.getHoleCards().isBlank()) {
                    holeCards = normalizeCards(player.getHoleCards());
                }

                Long stackDelta = contribution > 0 || action.getActionType() == ReplayActionType.RETURN_UNCALLED
                        ? nextStack
                        : null;
                playerUpdates.add(new ReplayPlayerUpdateResponse(seat, stackDelta, folded, inHand, holeCards));
            }

            long pot = computePot(currentStackByPlayer, metaData);
            List<ReplayPlayerUpdateResponse> compactUpdates = compactUpdates(playerUpdates);

            timeline.add(new ReplayTimelineFrameResponse(
                    street,
                    step,
                    pot,
                    parseCards(table.getCommunityCards()),
                    frameActions,
                    compactUpdates
            ));
            step++;
        }

        ReplayResultResponse result = parseResult(rawHandText, metaData, seatByPlayerName);
        return new ReplayResponse(players, result, timeline);
    }

    private List<ReplayPlayerBaseResponse> buildBasePlayers(HandMetaData metaData) {
        List<ReplayPlayerBaseResponse> base = new ArrayList<>();
        for (Player player : metaData.getPlayers()) {
            base.add(new ReplayPlayerBaseResponse(
                    player.getSeatNumber() == null ? 0 : player.getSeatNumber(),
                    player.getName(),
                    player.getChipStack(),
                    player.getPosition(),
                    player.isHero()
            ));
        }
        return base;
    }

    private Map<String, Long> buildBaseStackByPlayer(HandMetaData metaData) {
        Map<String, Long> baseStackByPlayer = new HashMap<>();
        Table preDeal = metaData.getSnapshot().get(Street.PRE_DEAL);
        List<Player> sourcePlayers = preDeal != null ? preDeal.getPlayers() : metaData.getPlayers();

        for (Player player : sourcePlayers) {
            baseStackByPlayer.put(player.getName(), player.getChipStack());
        }

        return baseStackByPlayer;
    }

    private Map<String, Integer> buildSeatByPlayerName(HandMetaData metaData) {
        Map<String, Integer> seatByPlayerName = new HashMap<>();
        for (Player player : metaData.getPlayers()) {
            if (player.getName() != null && player.getSeatNumber() != null) {
                seatByPlayerName.put(player.getName(), player.getSeatNumber());
            }
        }
        return seatByPlayerName;
    }

    private List<ReplayPlayerUpdateResponse> compactUpdates(List<ReplayPlayerUpdateResponse> updates) {
        Map<Integer, ReplayPlayerUpdateResponse> compacted = new LinkedHashMap<>();
        for (ReplayPlayerUpdateResponse update : updates) {
            ReplayPlayerUpdateResponse existing = compacted.get(update.getSeat());
            if (existing == null) {
                compacted.put(update.getSeat(), update);
                continue;
            }

            compacted.put(update.getSeat(), new ReplayPlayerUpdateResponse(
                    update.getSeat(),
                    update.getStack() != null ? update.getStack() : existing.getStack(),
                    update.getFolded() != null ? update.getFolded() : existing.getFolded(),
                    update.getInHand() != null ? update.getInHand() : existing.getInHand(),
                    update.getHoleCards() != null ? update.getHoleCards() : existing.getHoleCards()
            ));
        }
        return new ArrayList<>(compacted.values());
    }

    private long computePot(Map<String, Long> currentStackByPlayer, HandMetaData metaData) {
        long base = 0L;
        for (Player player : metaData.getPlayers()) {
            long initial = player.getChipStack();
            long current = currentStackByPlayer.getOrDefault(player.getName(), initial);
            base += Math.max(0L, initial - current);
        }
        return base;
    }

    private ReplayResultResponse parseResult(String rawHandText,
                                             HandMetaData metaData,
                                             Map<String, Integer> seatByPlayerName) {
        List<ReplayWinnerResponse> winners = new ArrayList<>();
        List<ReplayPotResponse> pots = new ArrayList<>();

        if (rawHandText == null || rawHandText.isBlank()) {
            return new ReplayResultResponse(winners, pots);
        }

        Map<Integer, ReplayWinnerResponse> winnersBySeat = new LinkedHashMap<>();
        String[] lines = rawHandText.replace("\r", "\n").split("\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isBlank()) {
                continue;
            }

            Matcher summaryWinnerMatch = SUMMARY_WIN_PATTERN.matcher(line);
            if (summaryWinnerMatch.find()) {
                int seat = Integer.parseInt(summaryWinnerMatch.group(1));
                String hand = normalizeCards(summaryWinnerMatch.group(3));
                long amount = parseAmount(summaryWinnerMatch.group(4) != null
                        ? summaryWinnerMatch.group(4)
                        : summaryWinnerMatch.group(6));
                String handDescription = summaryWinnerMatch.group(5);
                winnersBySeat.put(seat, new ReplayWinnerResponse(seat, amount, hand, handDescription));
                continue;
            }

            Matcher collectedMatch = COLLECTED_PATTERN.matcher(line);
            if (collectedMatch.find()) {
                String name = collectedMatch.group(1).trim();
                int seat = seatByPlayerName.getOrDefault(name, 0);
                if (seat == 0) {
                    continue;
                }

                Player player = findPlayer(metaData.getPlayers(), name);
                String hand = player == null ? null : normalizeCards(player.getHoleCards());
                long amount = parseAmount(collectedMatch.group(2));
                winnersBySeat.putIfAbsent(seat, new ReplayWinnerResponse(seat, amount, hand, null));
            }
        }

        winners.addAll(winnersBySeat.values());
        if (!winners.isEmpty()) {
            long total = 0L;
            Set<Integer> winnerSeats = new LinkedHashSet<>();
            for (ReplayWinnerResponse winner : winners) {
                total += winner.getAmount();
                winnerSeats.add(winner.getSeat());
            }
            pots.add(new ReplayPotResponse(total, new ArrayList<>(winnerSeats)));
        }

        return new ReplayResultResponse(winners, pots);
    }

    private Player findPlayer(List<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    private String toActionType(ReplayActionType actionType) {
        return switch (actionType) {
            case SMALL_BLIND -> "post_sb";
            case BIG_BLIND -> "post_bb";
            case RETURN_UNCALLED -> "return";
            default -> actionType.name().toLowerCase(Locale.ROOT);
        };
    }

    private Long toActionAmount(ReplayAction action) {
        if (action.getActionType() == ReplayActionType.CHECK || action.getActionType() == ReplayActionType.FOLD) {
            return null;
        }
        if (action.getActionType() == ReplayActionType.RAISE && action.getRaiseIncrement() != null) {
            return action.getAmount();
        }
        return action.getAmount();
    }

    private String normalizeCards(String cardsText) {
        if (cardsText == null || cardsText.isBlank()) {
            return null;
        }
        return String.join(" ", parseCards(cardsText));
    }

    private List<String> parseCards(String cardsText) {
        if (cardsText == null || cardsText.isBlank()) {
            return List.of();
        }

        String[] parts = cardsText.trim().split("\\s+");
        List<String> cards = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                cards.add(part);
            }
        }
        return cards;
    }

    private long contributionForPot(ReplayAction action) {
        return switch (action.getActionType()) {
            case ANTE, SMALL_BLIND, BIG_BLIND, CALL, BET -> action.getAmount();
            case RAISE -> action.getRaiseIncrement() == null ? action.getAmount() : action.getRaiseIncrement();
            case RETURN_UNCALLED -> -action.getAmount();
            default -> 0L;
        };
    }

    private Map<Street, List<ReplayAction>> parseActionsByStreet(String rawHandText) {
        Map<Street, List<ReplayAction>> actionsByStreet = new EnumMap<>(Street.class);
        for (Street street : REPLAY_STREETS) {
            actionsByStreet.put(street, new ArrayList<>());
        }

        if (rawHandText == null || rawHandText.isBlank()) {
            return actionsByStreet;
        }

        Street currentStreet = Street.PREFLOP;
        String[] lines = rawHandText.replace("\r", "\n").split("\n");

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isBlank()) {
                continue;
            }

            if (line.matches("^\\*\\*\\*\\s*FLOP.*")) {
                currentStreet = Street.FLOP;
                continue;
            }
            if (line.matches("^\\*\\*\\*\\s*TURN.*")) {
                currentStreet = Street.TURN;
                continue;
            }
            if (line.matches("^\\*\\*\\*\\s*RIVER.*")) {
                currentStreet = Street.RIVER;
                continue;
            }
            if (line.matches("^\\*\\*\\*\\s*SHOW\\s*DOWN.*")) {
                currentStreet = Street.SHOWDOWN;
                continue;
            }

            Matcher uncalledMatch = UNCALLED_BET_PATTERN.matcher(line);
            if (uncalledMatch.find()) {
                long amount = parseAmount(uncalledMatch.group(1));
                String actor = uncalledMatch.group(2).trim();
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.RETURN_UNCALLED, amount, null));
                continue;
            }

            Matcher matcher = ACTION_LINE_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }

            String actor = matcher.group(1).trim();
            String actionText = matcher.group(2).trim();

            if (actionText.matches("(?i)^posts\\s+the\\s+ante\\s+.+")) {
                long amount = parseAmount(actionText.replaceFirst("(?i)^posts\\s+the\\s+ante\\s+", ""));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.ANTE, amount, null));
                continue;
            }

            if (actionText.matches("(?i)^posts\\s+small\\s+blind\\s+.+")) {
                long amount = parseAmount(actionText.replaceFirst("(?i)^posts\\s+small\\s+blind\\s+", ""));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.SMALL_BLIND, amount, null));
                continue;
            }

            if (actionText.matches("(?i)^posts\\s+big\\s+blind\\s+.+")) {
                long amount = parseAmount(actionText.replaceFirst("(?i)^posts\\s+big\\s+blind\\s+", ""));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.BIG_BLIND, amount, null));
                continue;
            }

            if (actionText.matches("(?i)^folds.*")) {
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.FOLD, 0L, null));
                continue;
            }

            if (actionText.matches("(?i)^checks.*")) {
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.CHECK, 0L, null));
                continue;
            }

            if (actionText.matches("(?i)^calls\\s+.+")) {
                long amount = parseAmount(actionText.replaceFirst("(?i)^calls\\s+", ""));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.CALL, amount, null));
                continue;
            }

            Matcher raiseMatch = RAISE_PATTERN.matcher(actionText);
            if (raiseMatch.find()) {
                long increment = parseAmount(raiseMatch.group(1));
                long amount = parseAmount(raiseMatch.group(2));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.RAISE, amount, increment));
                continue;
            }

            if (actionText.matches("(?i)^bets\\s+.+")) {
                long amount = parseAmount(actionText.replaceFirst("(?i)^bets\\s+", ""));
                actionsByStreet.get(currentStreet).add(new ReplayAction(currentStreet, actor, ReplayActionType.BET, amount, null));
            }
        }

        return actionsByStreet;
    }

    private long parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0L;
        }

        String cleaned = raw.replaceAll("[^\\d.,-]", "").replace(',', '.');
        if (cleaned.isBlank()) {
            return 0L;
        }

        try {
            return Math.round(Double.parseDouble(cleaned));
        } catch (NumberFormatException exception) {
            return 0L;
        }
    }
}
