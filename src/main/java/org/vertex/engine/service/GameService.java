package org.vertex.engine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertex.engine.animations.ToastAnimation;
import org.vertex.engine.enums.*;
import org.vertex.engine.interfaces.Ruleset;
import org.vertex.engine.manager.SaveManager;
import org.vertex.engine.records.Save;
import org.vertex.engine.render.RenderContext;

import java.time.LocalDate;
import java.util.ArrayList;

public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private static GameMenu gameMenu;
    private static GameState state;
    private static PlayState mode;
    private static Games game;
    private static Tint currentTurn;
    private static RenderContext render;
    private static BoardService boardService;
    private static ServiceFactory service;
    private static SaveManager saveManager;

    public GameService(RenderContext render, BoardService boardService, SaveManager saveManager) {
        GameService.render = render;
        GameService.boardService = boardService;
        GameService.saveManager = saveManager;
        game = Games.CHESS;
    }

    public static GameMenu getGameMenu() { return gameMenu; }
    public static void setGameMenu(GameMenu menu) { gameMenu = menu; }

    public static void setGame(Games g) { game = g; }
    public static Games getGame() { return game; }

    public static GameState getState() { return state; }
    public static void setState(GameState s) { state = s; }

    public static PlayState getMode() { return mode; }
    public static Tint getCurrentTurn() { return currentTurn; }
    public static void setCurrentTurn(Tint tint) { currentTurn = tint; }

    public static ServiceFactory getServiceFactory() { return service; }
    public void setServiceFactory(ServiceFactory svc) { service = svc; }

    public SaveManager getSaveManager() { return saveManager; }
    public void setSaveManager(SaveManager sm) { saveManager = sm; }

    public void startNewGame() {
        setCurrentTurn(Tint.LIGHT);
        service.getMovesManager().setMoves(new ArrayList<>());
        BooleanService.isCheckmate = false;
        BooleanService.isPromotionActive = false;
        boardService.startBoard();
        Save newSave = new Save(
                getGame(),
                LocalDate.now().toString(),
                getCurrentTurn(),
                service.getPieceService().getPieces(),
                service.getAchievementService().getUnlockedAchievements()
        );
        saveManager.saveGame(newSave);
        log.info("New game started and autosave created.");
        Ruleset rule = service.getModelService().createRuleSet(game);
        service.getModelService().setRule(rule);
        setState(GameState.BOARD);
    }

    public void continueGame() {
        if (!saveManager.autosaveExists()) {
            log.warn("No autosave found. Starting new game.");
            startNewGame();
            return;
        }
        Save loaded = saveManager.loadGame();
        if (loaded == null || loaded.pieces() == null) {
            log.warn("Autosave invalid or empty. Starting new game.");
            startNewGame();
            return;
        }
        boardService.restoreSprites(loaded, service.getGuiService());
        service.getPieceService().getPieces().clear();
        service.getPieceService().getPieces().addAll(loaded.pieces());
        service.getAchievementService().setUnlockedAchievements(loaded.achievements());
        setCurrentTurn(loaded.player());

        service.getTimerService().start();
        log.info("Autosave loaded successfully.");
        setState(GameState.BOARD);
    }

    public static void autoSave() {
        Save save = new Save(
                getGame(),
                LocalDate.now().toString(),
                getCurrentTurn(),
                service.getPieceService().getPieces(),
                service.getAchievementService().getUnlockedAchievements()
        );
        saveManager.saveGame(save);
        log.debug("Autosave triggered.");
    }

    public static void nextGame() {
        Games[] games = Games.values();
        int nextIndex = (game.ordinal() + 1) % games.length;
        setGame(games[nextIndex]);
        service.getAnimationService().add(new ToastAnimation(games[nextIndex].getLabel()));
    }
}