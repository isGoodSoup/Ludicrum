package org.lud.engine.enums;

public enum UIButton {
    BUTTON(""),
    BUTTON_SMALL("small"),
    UNDO("undo"),
    RESET("reset"),
    ACHIEVEMENTS("achievements"),
    SETTINGS("settings"),
    EXIT("exit"),
    NEXT_PAGE("next_page"),
    PREVIOUS_PAGE("previous_page");

    private final String suffix;

    UIButton(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}