package org.lud.engine.enums;

import org.lud.engine.util.Colors;
import org.lud.engine.interfaces.Clickable;
import org.lud.engine.service.GameService;
import org.lud.engine.service.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GameMenu implements Clickable {
    PLAY("menu.play", "tooltip.play", "tooltip.play_continue") {
        @Override
        public void run(GameService gameService) {
            GameService.getGame().setup(gameService);
        }
    },
    GAMES("menu.games", "tooltip.games", "tooltip.games_continue") {
        @Override
        public void run(GameService gameService) {
            gameService.nextGame();
        }
    },
    SETTINGS("menu.settings", "tooltip.settings", "tooltip.settings_continue") {
        @Override
        public void run(GameService gameService) {
            gameService.setState(GameState.SETTINGS);
            log.debug("Settings menu");
        }
    },
    ACHIEVEMENTS("menu.achievements", "tooltip.achievements", "tooltip.achievements_continue") {
        @Override
        public void run(GameService gameService) {
            gameService.setState(GameState.ACHIEVEMENTS);
            log.debug("Achievements menu");
        }
    },
    LANG("menu.lang", "tooltip.lang", "tooltip.lang_continue") {
        @Override
        public void run(GameService gameService) {
            Lang.nextLang();
        }
    },
    THEME("menu.theme", "tooltip.theme", "tooltip.theme_continue") {
        @Override
        public void run(GameService gameService) {
            Colors.nextTheme();
        }
    },
    EXIT("menu.exit", "tooltip.exit", "tooltip.exit_continue") {
        @Override
        public void run(GameService gameService) {
            System.exit(0);
        }
    };

    private static final Logger log = LoggerFactory.getLogger(GameMenu.class);
    private final String labelKey;
    private final String tooltipKey;
    private final String continueTooltipKey;

    GameMenu(String labelKey, String tooltipKey, String continueTooltipKey) {
        this.labelKey = labelKey;
        this.tooltipKey = tooltipKey;
        this.continueTooltipKey = continueTooltipKey;
    }

    public String getLabel() {
        return Localization.lang.t(labelKey);
    }

    public String getTooltip() {
        return Localization.lang.t(tooltipKey);
    }

    public String getContinueTooltip() {
        return Localization.lang.f(continueTooltipKey, GameService.getGame().getLabel());
    }

    public abstract void run(GameService gameService);


    @Override
    public void onClick(GameService gameService) {
        run(gameService);
    }
}