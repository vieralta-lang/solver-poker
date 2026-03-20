package org.torinelli.api.dto.replay;

import org.torinelli.domain.enums.Street;

public class ReplayAction {
    private final Street street;
    private final String actor;
    private final ReplayActionType actionType;
    private final long amount;
    private final Long raiseIncrement;

    public ReplayAction(Street street, String actor, ReplayActionType actionType, long amount, Long raiseIncrement) {
        this.street = street;
        this.actor = actor;
        this.actionType = actionType;
        this.amount = amount;
        this.raiseIncrement = raiseIncrement;
    }

    public Street getStreet() {
        return street;
    }

    public String getActor() {
        return actor;
    }

    public ReplayActionType getActionType() {
        return actionType;
    }

    public long getAmount() {
        return amount;
    }

    public Long getRaiseIncrement() {
        return raiseIncrement;
    }
}
