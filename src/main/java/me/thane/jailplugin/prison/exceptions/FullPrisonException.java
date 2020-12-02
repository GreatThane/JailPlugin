package me.thane.jailplugin.prison.exceptions;

import me.thane.jailplugin.prison.Prison;

public class FullPrisonException extends Exception {
    private final Prison prison;

    public FullPrisonException(String message, Prison prison) {
        super(message);
        this.prison = prison;
    }

    public Prison getPrison() {
        return prison;
    }
}
