package org.lud.engine.enums;

public enum UIButton {
    BUTTON(""),
    BUTTON_SMALL("small"),
    BUTTON_BIG("big"),
    UNDO("undo"),
    RESET("reset"),
    ACHIEVEMENTS("achievements"),
    SETTINGS("settings"),
    EXIT("exit"),
    NEXT_PAGE("next_page"),
    PREVIOUS_PAGE("previous_page"),
    PAUSE("pause"),
    SWITCH("switch");

    private final String suffix;

    UIButton(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}