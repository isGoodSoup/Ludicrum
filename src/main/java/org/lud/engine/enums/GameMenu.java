package org.lud.engine.enums;

import org.lud.engine.interfaces.Clickable;
import org.lud.engine.service.GameService;
import org.lud.engine.service.Localization;
import org.lud.engine.service.ServiceFactory;
import org.lud.engine.util.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ALL")
public enum GameMenu implements Clickable {
    PLAY("menu.play", "tooltip.play", "tooltip.play_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                Games selected = gameService.getSelectedGame();
                if(selected == null) { selected = Games.CHESS; }
                selected.setup(gameService);
                clickFX(gameService.getServiceFactory());
                wasRan = true;
            }
        }
    },
    GAMES("menu.games", "tooltip.games", "tooltip.games_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                gameService.nextGame();
                clickFX(gameService.getServiceFactory());
                wasRan = true;
            }
        }
    },
    ACHIEVEMENTS("menu.achievements", "tooltip.achievements", "tooltip.achievements_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                gameService.setState(GameState.ACHIEVEMENTS);
                clickFX(gameService.getServiceFactory());
                log.debug("Achievements menu");
                wasRan = true;
            }
        }
    },
    SETTINGS("menu.settings", "tooltip.settings", "tooltip.settings_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                gameService.setState(GameState.SETTINGS);
                clickFX(gameService.getServiceFactory());
                log.debug("Settings menu");
                wasRan = true;
            }
        }
    },
    LANG("menu.lang", "tooltip.lang", "tooltip.lang_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                Lang.nextLang();
                clickFX(gameService.getServiceFactory());
                wasRan = true;
            }
        }
    },
    EXIT("menu.exit", "tooltip.exit", "tooltip.exit_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                clickFX(gameService.getServiceFactory());
                System.exit(0);
                wasRan = true;
            }
        }
    },
    THEME("menu.theme", "tooltip.theme", "tooltip.theme_continue") {
        @Override
        public void run(GameService gameService) {
            if(!wasRan) {
                Colors.nextTheme();
                clickFX(gameService.getServiceFactory());
                wasRan = true;
            }
        }
    };

    private static final Logger log = LoggerFactory.getLogger(GameMenu.class);

    private final String labelKey;
    private final String tooltipKey;
    private final String continueTooltipKey;
    private static boolean wasRan = false;

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

    public void clickFX(ServiceFactory service) {
        service.getSound().playFX(0);
    }

    @Override
    public void onClick(GameService gameService) {
        run(gameService);
    }

    public void reset() {
        wasRan = false;
    }
}