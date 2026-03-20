package org.torinelli.resources;

import jakarta.ws.rs.Path;

@Path("/api/equity")
public class EquityCalculator {

    public long retrieveEquity(long totalPot, long playerInvestment) {
        if (totalPot == 0) {
            return 0L;
        }
        return (playerInvestment * 100) / totalPot;

    }

}
