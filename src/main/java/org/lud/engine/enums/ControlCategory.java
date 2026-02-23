package org.lud.engine.enums;

import org.lud.engine.service.Localization;

public enum ControlCategory {
    GLOBAL("global"),
    MENU("menu"),
    BOARD_KEYBOARD("board_keyboard"),
    BOARD_MOUSE("board_mouse"),
    ACCESSIBILITY("accessibility"),
    SANDBOX("sandbox");

    private final String key;

    ControlCategory(String key) {
        this.key = key;
    }

    public String getLabel() {
        return Localization.lang.t("control.category." + key);
    }
}