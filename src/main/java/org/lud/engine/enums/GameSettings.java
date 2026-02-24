package org.lud.engine.enums;

import org.lud.engine.interfaces.Clickable;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.Localization;

public enum GameSettings implements Clickable {
    AI_OPPONENT("settings.ai_opponent") {
        public boolean get() { return BooleanService.canAIPlay; }
        public void toggle() { BooleanService.canAIPlay ^= true; }
    },

    HELP("settings.help") {
        public boolean get() { return BooleanService.canToggleHelp; }
        public void toggle() { BooleanService.canToggleHelp ^= true; }
    },

    SAVES("settings.saves") {
        public boolean get() { return BooleanService.canSave; }
        public void toggle() { BooleanService.canSave ^= true; }
    },

    ACHIEVEMENTS("settings.achievements") {
        public boolean get() { return BooleanService.canDoAchievements; }
        public void toggle() { BooleanService.canDoAchievements ^= true; }
    },

    PLAY_MUSIC("settings.play_music") {
        public boolean get() { return BooleanService.canPlayMusic; }
        public void toggle() { BooleanService.canPlayMusic ^= true; }
    },

    BASIC_MOVES("settings.basic_moves") {
        public boolean get() { return BooleanService.canDoMoves; }
        public void toggle() { BooleanService.canDoMoves ^= true; }
    },

    PROMOTION("settings.promotion") {
        public boolean get() { return BooleanService.canPromote; }
        public void toggle() { BooleanService.canPromote ^= true; }
    },

    AUTOMATIC("settings.automatic") {
        @Override
        public boolean get() { return BooleanService.canDoAuto; }
        @Override
        public void toggle() { BooleanService.canDoAuto ^= true; }
    },

    TIMER("settings.timer") {
        public boolean get() { return BooleanService.canTime; }
        public void toggle() { BooleanService.canTime ^= true; BooleanService.canStopwatch = false; }
    },

    STOPWATCH("settings.stopwatch") {
        public boolean get() { return BooleanService.canStopwatch; }
        public void toggle() { BooleanService.canStopwatch ^= true; BooleanService.canTime = false; }
    },

    UNDO_MOVES("settings.undo_moves") {
        public boolean get() { return BooleanService.canUndoMoves; }
        public void toggle() { BooleanService.canUndoMoves ^= true; BooleanService.canDoAchievements = false; }
    },

    RESET_TABLE("settings.reset_table") {
        public boolean get() { return BooleanService.canResetTable; }
        public void toggle() { BooleanService.canResetTable ^= true; }
    },

    SHOW_TICK("settings.show_tick") {
        public boolean get() { return BooleanService.canShowTick; }
        public void toggle() { BooleanService.canShowTick ^= true; }
    },

    THEMES("settings.themes") {
        public boolean get() { return BooleanService.canTheme; }
        public void toggle() { BooleanService.canTheme ^= true; }
    },

    COLORBLIND_MODE("settings.colorblind_mode") {
        public boolean get() { return BooleanService.canBeColorblind; }
        public void toggle() { BooleanService.canBeColorblind ^= true; }
    },

    HARD_MODE("settings.hard_mode") {
        public boolean get() { return BooleanService.canDoHard; }
        public void toggle() {
            BooleanService.canDoHard ^= true;
            BooleanService.canDoAuto = true;
            BooleanService.canTime = true;
            BooleanService.canStopwatch = false;
            BooleanService.canDoAchievements = true;
            BooleanService.canSave = false;
            BooleanService.canShowTick = false;
            BooleanService.canUndoMoves = false;
            BooleanService.canResetTable = false;
        }
    };

    private final String key;

    GameSettings(String key) {
        this.key = key;
    }

    public String getLabel() {
        return Localization.lang.t(key);
    }

    public abstract boolean get();
    public abstract void toggle();

    @Override
    public void onClick(GameService gameService) {
        toggle();
    }
}