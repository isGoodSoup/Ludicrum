package org.vertex.engine.enums;

import org.vertex.engine.service.GameService;

public enum Games {
    CHESS("CHESS", "",
            "A classic strategy duel where every piece moves differently. " +
                    "Outsmart your opponent and deliver checkmate to win") {
        @Override
        public void setup(GameService gameService) {
            GameService.setGame(this);
            GameService.setState(GameState.BOARD);
            gameService.startNewGame();
        }

        @Override
        public int getBoardSize() {
            return 8;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    },
    CHECKERS("CHECKERS", "checker_",
            "A fast-paced tactical game of diagonal moves and jumps. Capture " +
                    "all opponent pieces or block their moves to win") {
        @Override
        public void setup(GameService gameService) {
            GameService.setGame(this);
            GameService.setState(GameState.BOARD);
            gameService.startNewGame(); // TODO
        }

        @Override
        public int getBoardSize() {
            return 8;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    };

    private final String label;
    private final String spritePrefix;
    private final String tooltip;

    Games(String label, String spritePrefix, String tooltip) {
        this.label = label;
        this.spritePrefix = spritePrefix;
        this.tooltip = tooltip;
    }

    public String getLabel() {
        return label;
    }

    public String getSpritePrefix() {
        return spritePrefix;
    }

    public String getTooltip() {
        return tooltip;
    }

    public abstract boolean isEnabled();

    public abstract int getBoardSize();

    public abstract void setup(GameService gameService);
}
