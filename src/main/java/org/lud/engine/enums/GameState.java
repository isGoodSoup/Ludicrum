package org.lud.engine.enums;

import org.lud.engine.interfaces.State;

public enum GameState implements State {
    INTRO, MENU, BOARD, SETTINGS, ACHIEVEMENTS, CHECKMATE, STALEMATE, VICTORY
}
