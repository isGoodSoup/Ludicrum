package org.lud.engine.enums;

import org.lud.engine.gui.Colors;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GameMenu implements Clickable {
    PLAY("PLAY", "Start a match of ", "Continue match of ") {
        @Override
        public void run(GameService gameService) {
            GameService.getGame().setup(gameService);
            gameService.getServiceFactory().getSound().playFX(7);
        }
    },
    GAMES("GAMES", "", "Who knows, maybe there's more?") {
        @Override
        public void run(GameService gameService) {
            gameService.nextGame();
        }
    },
    SETTINGS("SETTINGS", "Settings, themes, toggles", "") {
        @Override
        public void run(GameService gameService) {
            gameService.setState(GameState.SETTINGS);
        }
    },
    ADVANCEMENTS("ACHIEVEMENTS", "Track your progress", "") {
        @Override
        public void run(GameService gameService) {
            gameService.setState(GameState.ACHIEVEMENTS);
        }
    },
    THEME("THEMES", "Change the look and feel", ""){
        @Override
        public void run(GameService gameService) {
            Colors.nextTheme();
        }
    },
    EXIT("EXIT", "Leave?", "") {
        @Override
        public void run(GameService gameService) {
            log.info("Ending session");
            System.exit(0);
        }
    };

    private static final Logger log = LoggerFactory.getLogger(GameMenu.class);
    private final String label;
    private final String tooltip;
    private final String continueTooltip;

    GameMenu(String label, String tooltip, String continueTooltip) {
        this.label = label;
        this.tooltip = tooltip;
        this.continueTooltip = continueTooltip;
    }

    public String getLabel() {
        return label;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getContinueTooltip() {
        return continueTooltip;
    }

    public boolean isEnabled(GameService gameService) {
        return true;
    }

    public abstract void run(GameService gameService);

    @Override
    public void onClick(GameService gameService) {
        run(gameService);
    }
}
