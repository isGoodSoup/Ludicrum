package org.lud.engine.enums;

public enum ControlCategory {
    GLOBAL("Global"),
    MENU("Menu Navigation"),
    BOARD_KEYBOARD("Board - Keyboard"),
    BOARD_MOUSE("Board - Mouse"),
    ACCESSIBILITY("Accessibility"),
    SANDBOX("Sandbox / Developer");

    private final String label;

    ControlCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
