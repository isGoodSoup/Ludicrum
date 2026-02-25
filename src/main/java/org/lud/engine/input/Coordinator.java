package org.lud.engine.input;

import org.lud.engine.enums.LastInput;

public class Coordinator {
    private LastInput lastInput;

    public LastInput getLastInput() {
        return lastInput;
    }

    public void setLastInput(LastInput lastInput) {
        this.lastInput = lastInput;
    }
}
