package org.torinelli.parser;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class PotTracker {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public long calculateActionContribution(String playerName, String action, String details, ParsingState state) {
        if (details == null || details.isBlank()) {
            return 0L;
        }

        String normalizedAction = action.toLowerCase();
        if ("folds".equals(normalizedAction) || "checks".equals(normalizedAction)) {
            return 0L;
        }

        List<Long> numbers = NUMBER_PATTERN.matcher(details)
                .results()
                .map(MatchResult::group)
                .map(Long::parseLong)
                .toList();

        if (numbers.isEmpty()) {
            return 0L;
        }

        if ("raises".equals(normalizedAction)) {
            long targetAmount = numbers.get(numbers.size() - 1);
            long alreadyInvested = state.getStreetInvested().getOrDefault(playerName, 0L);
            long contribution = Math.max(0L, targetAmount - alreadyInvested);
            state.getStreetInvested().put(playerName, alreadyInvested + contribution);
            return contribution;
        }

        long contribution = numbers.get(0);
        if ("calls".equals(normalizedAction) || "bets".equals(normalizedAction)) {
            // pegue o investido na street e some o contribution, para garantir que o valor investido seja atualizado corretamente
            state.getStreetInvested().merge(playerName, contribution, Long::sum);
        }
        return contribution;
    }

    public void registerBlind(String playerName, long amount, ParsingState state) {
        state.getStreetInvested().merge(playerName, amount, Long::sum);
    }
}
