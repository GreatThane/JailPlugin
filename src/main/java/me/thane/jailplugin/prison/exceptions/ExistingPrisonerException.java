package me.thane.jailplugin.prison.exceptions;

import me.thane.jailplugin.prison.Prisoner;

public class ExistingPrisonerException extends Exception {
    private final Prisoner prisoner;

    public ExistingPrisonerException(String message, Prisoner prisoner) {
        super(message);
        this.prisoner = prisoner;
    }

    public Prisoner getPrisoner() {
        return prisoner;
    }
}
