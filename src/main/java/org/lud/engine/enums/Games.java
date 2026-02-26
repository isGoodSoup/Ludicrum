package org.lud.engine.enums;

import org.lud.engine.entities.Board;
import org.lud.engine.interfaces.State;
import org.lud.engine.service.BooleanService;
import org.lud.engine.service.GameService;
import org.lud.engine.service.Localization;

@SuppressWarnings("ALL")
public enum Games implements State {
    CHESS("game.chess", "checker_", "tooltip.chess") {
        @Override
        public void setup(GameService gameService) {
            gameService.setGame(this);
            gameService.setState(GameState.BOARD);
            if(!gameService.getSaveManager().autosaveExists()) {
                gameService.startNewGame(this);
            } else {
                gameService.continueGame();
            }
        }

        @Override
        public int getBoardSize(Board board, GameService gameService) {
            return board.getGrids().get(GameService.getGame());
        }

        @Override
        public boolean isEnabled() { return true; }
    },
    CHECKERS("game.checkers", "checker_", "tooltip.checkers") {
        @Override
        public void setup(GameService gameService) {
            gameService.setGame(this);
            gameService.setState(GameState.BOARD);
            if(!gameService.getSaveManager().autosaveExists()) {
                gameService.startNewGame(this);
            } else {
                gameService.continueGame();
            }
        }

        @Override
        public int getBoardSize(Board board, GameService gameService) {
            return board.getGrids().get(GameService.getGame());
        }

        @Override
        public boolean isEnabled() { return true; }
    },
    SHOGI("game.shogi", "shogi_", "tooltip.shogi") {
        @Override
        public void setup(GameService gameService) {
            gameService.setGame(this);
            gameService.setState(GameState.BOARD);
            if(!gameService.getSaveManager().autosaveExists()) {
                gameService.startNewGame(this);
            } else {
                gameService.continueGame();
            }
        }

        @Override
        public int getBoardSize(Board board, GameService gameService) {
            return board.getGrids().get(GameService.getGame());
        }

        @Override
        public boolean isEnabled() { return true; }
    },
    SANDBOX("game.sandbox", "", "tooltip.sandbox") {
        @Override
        public void setup(GameService gameService) {
            BooleanService.canAIPlay = false;
            gameService.setCurrentTurn(Turn.LIGHT);
            gameService.setGame(this);
            gameService.setState(GameState.BOARD);

            if(!gameService.getSaveManager().autosaveExists()) {
                gameService.startNewGame(this);
            } else {
                gameService.continueGame();
            }
        }

        @Override
        public int getBoardSize(Board board, GameService gameService) {
            return board.getGrids().get(GameService.getGame());
        }

        @Override
        public boolean isEnabled() { return true; }
    },
    CHAOS("game.chaos", "", "tooltip.chaos") {
        @Override
        public void setup(GameService gameService) {
            gameService.setCurrentTurn(Turn.LIGHT);
            gameService.setGame(this);
            gameService.setState(GameState.BOARD);

            if(!gameService.getSaveManager().autosaveExists()) {
                gameService.startNewGame(this);
            } else {
                gameService.continueGame();
            }
        }

        @Override
        public int getBoardSize(Board board, GameService gameService) {
            return board.getGrids().get(GameService.getGame());
        }

        @Override
        public boolean isEnabled() { return true; }
    };

    private final String labelKey;
    private final String spritePrefix;
    private final String tooltipKey;

    Games(String labelKey, String spritePrefix, String tooltipKey) {
        this.labelKey = labelKey;
        this.spritePrefix = spritePrefix;
        this.tooltipKey = tooltipKey;
    }

    public String getLabel() {
        return Localization.lang.t(labelKey);
    }

    public String getSpritePrefix() { return spritePrefix; }

    public String getTooltip() {
        return Localization.lang.t(tooltipKey);
    }

    public abstract boolean isEnabled();

    public abstract int getBoardSize(Board board, GameService gameService);

    public abstract void setup(GameService gameService);
}