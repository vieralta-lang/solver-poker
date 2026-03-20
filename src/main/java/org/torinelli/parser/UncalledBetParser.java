package org.torinelli.parser;

import java.util.regex.Matcher;

public class UncalledBetParser {

    public long parseReturnedAmount(String line) {
        Matcher m = ParserPatterns.UNCALLED_BET_PATTERN.matcher(line);
        if (!m.find()) {
            return 0L;
        }

        return Long.parseLong(m.group(1));
    }
}
