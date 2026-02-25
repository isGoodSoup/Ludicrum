package org.lud.engine.enums;

import org.lud.engine.service.Localization;

public enum ControlsHUD {

    CHANGE_THEME(ControlCategory.GLOBAL, "change_theme", new String[]{"ctrl", "t"}),
    CHANGE_LANG(ControlCategory.GLOBAL, "change_lang", new String[]{"ctrl", "l"}),
    INCREASE_VOLUME(ControlCategory.GLOBAL, "volume_up", new String[]{"ctrl", "k"}),
    DECREASE_VOLUME(ControlCategory.GLOBAL, "volume_down", new String[]{"ctrl", "m"}),
    MUTE(ControlCategory.GLOBAL, "mute_music", new String[]{"m"}),
    TOGGLE_FULLSCREEN(ControlCategory.GLOBAL, "fullscreen", new String[]{"f11"}),
    QUIT_GAME(ControlCategory.GLOBAL, "quit", new String[]{"ctrl", "q"}),

    SELECT(ControlCategory.MENU, "select", new String[]{"enter"}),
    BACK_TO_MENU(ControlCategory.MENU, "back_to_menu", new String[]{"escape"}),
    NAVIGATE_UP(ControlCategory.MENU, "up", new String[]{"arrow_up"}),
    NAVIGATE_DOWN(ControlCategory.MENU, "down", new String[]{"arrow_down"}),
    NAVIGATE_LEFT(ControlCategory.MENU, "left", new String[]{"arrow_left"}),
    NAVIGATE_RIGHT(ControlCategory.MENU, "right", new String[]{"arrow_right"}),
    CHANGE_GAME(ControlCategory.MENU, "switch_game", new String[]{"ctrl", "g"}),

    SWITCH_TURNS(ControlCategory.BOARD_KEYBOARD, "switch_turns", new String[]{"tab"}),
    RESET_BOARD(ControlCategory.BOARD_KEYBOARD, "reset_board", new String[]{"ctrl", "r"}),
    UNDO_MOVE(ControlCategory.BOARD_KEYBOARD, "undo_move", new String[]{"ctrl", "z"}),
    CANCEL_MOVE(ControlCategory.BOARD_KEYBOARD, "cancel_move", new String[]{"c"}),
    TOGGLE_MOVES_LIST(ControlCategory.BOARD_KEYBOARD, "toggle_moves_list", new String[]{"ctrl", "h"}),

    DRAG_PIECE(ControlCategory.BOARD_MOUSE, "drag_piece", new String[]{"mouse_left"}),

    PROTANOPIA(ControlCategory.ACCESSIBILITY, "protanopia", new String[]{"1"}),
    DEUTERANOPIA(ControlCategory.ACCESSIBILITY, "deuteranopia", new String[]{"2"}),
    TRITANOPIA(ControlCategory.ACCESSIBILITY, "tritanopia", new String[]{"3"}),

    TOGGLE_SANDBOX(ControlCategory.SANDBOX, "toggle_sandbox", new String[]{"ctrl", "s"}),
    EXECUTE_CONSOLE(ControlCategory.SANDBOX, "execute_console", new String[]{"ctrl", "enter"});

    private final ControlCategory category;
    private final String key;
    private final String[] keys;

    ControlsHUD(ControlCategory category, String key, String[] keys) {
        this.category = category;
        this.key = key;
        this.keys = keys;
    }

    public ControlCategory getCategory() {
        return category;
    }

    public String getAction() {
        return Localization.lang.t("control.action." + key);
    }

    public String[] getKeys() {
        return keys;
    }
}