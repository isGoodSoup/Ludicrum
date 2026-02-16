package org.vertex.engine.enums;

import org.vertex.engine.service.GameService;

public enum GameMenu {
    PLAY("PLAY") {
        @Override
        public void run(GameService gameService) {
            GameService.getGame().setup(gameService);
        }
    },
    SETTINGS("SETTINGS") {
        @Override
        public void run(GameService gameService) {
            GameService.setState(GameState.RULES);
        }
    },
    ADVANCEMENTS("ACHIEVEMENTS") {
        @Override
        public void run(GameService gameService) {
            GameService.setState(GameState.ACHIEVEMENTS);
        }
    },
    EXIT("EXIT") {
        @Override
        public void run(GameService gameService) {
            System.exit(0);
        }
    };

    private final String label;

    GameMenu(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled(GameService gameService) {
        return true;
    }

    public abstract void run(GameService gameService);
}
