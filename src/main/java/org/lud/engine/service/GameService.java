package org.lud.engine.service;

import org.lud.engine.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lud.engine.interfaces.Ruleset;
import org.lud.engine.manager.SaveManager;
import org.lud.engine.records.Save;
import org.lud.engine.render.RenderContext;

import java.time.LocalDate;
import java.util.ArrayList;

public class GameService {
    private GameMenu gameMenu;
    private GameState state;
    private PlayState mode;
    private static Games games;
    private Games previousGame;
    private Turn currentTurn;
    private RenderContext render;
    private BoardService boardService;
    private ServiceFactory service;
    private SaveManager saveManager;
    private final Logger log = LoggerFactory.getLogger(GameService.class);


    public GameService(RenderContext render, BoardService boardService, SaveManager saveManager) {
        this.render = render;
        this.boardService = boardService;
        this.saveManager = saveManager;
        games = Games.CHESS;
    }

    public GameMenu getGameMenu() { return gameMenu; }
    public void setGameMenu(GameMenu menu) { gameMenu = menu; }

    public void setGame(Games games) { GameService.games = games; }
    public static Games getGame() { return games; }

    public Games getPreviousGame() { return previousGame; }
    public void setPreviousGame(Games previousGame) { this.previousGame = previousGame; }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public PlayState getMode() { return mode; }
    public Turn getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(Turn turn) { currentTurn = turn; }

    public ServiceFactory getServiceFactory() { return service; }
    public void setServiceFactory(ServiceFactory service) { this.service = service; }

    public SaveManager getSaveManager() { return saveManager; }
    public void setSaveManager(SaveManager saveManager) { this.saveManager = saveManager; }

    public BoardService getBoardService() { return boardService; }
    public void setBoardService(BoardService boardService) { this.boardService = boardService; }

    public String getTooltip(Games game, boolean hasSave) {
        String base = hasSave
                ? GameMenu.PLAY.getContinueTooltip()
                : GameMenu.PLAY.getTooltip();
        return base + game.getLabel();
    }

    public void startNewGame() {
        setCurrentTurn(Turn.LIGHT);
        service.getMovesManager().setMoves(new ArrayList<>());
        BooleanService.isCheckmate = false;
        BooleanService.isPromotionActive = false;
        boardService.prepBoard();
        boardService.startBoard();
        Save newSave = new Save(
                getGame(),
                LocalDate.now().toString(),
                getCurrentTurn(),
                service.getPieceService().getPieces(),
                service.getAchievementService().getUnlockedAchievements()
        );
        saveManager.saveGame(newSave);
        if(!(GameService.getGame() == Games.SANDBOX)) {
            Ruleset rule = service.getModelService().createRuleSet(games);
            service.getModelService().setRule(rule);
        }
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
        if(loaded.game() != getGame()) {
            log.info("Switching game mode to match save: {}", loaded.game());
        }
        setGame(loaded.game());
        boardService.getBoard().setSize(loaded.game());
        boardService.prepBoard();
        boardService.restoreSprites(loaded, service.getGuiService());
        service.getPieceService().getPieces().clear();
        service.getPieceService().getPieces().addAll(loaded.pieces());
        service.getAchievementService().setUnlockedAchievements(loaded.achievements());
        setCurrentTurn(loaded.player());

        if(!(games == Games.SANDBOX)) {
            Ruleset rule = service.getModelService().createRuleSet(getGame());
            service.getModelService().setRule(rule);
        }

        service.getTimerService().start();
        log.info("Autosave loaded successfully.");
        setState(GameState.BOARD);
    }

    public void autoSave() {
        if(!BooleanService.canSave) { return; }
        Save save = new Save(
                getGame(),
                LocalDate.now().toString(),
                getCurrentTurn(),
                service.getPieceService().getPieces(),
                service.getAchievementService().getUnlockedAchievements()
        );
        saveManager.saveGame(save);
    }

    public void nextGame() {
        Games[] games = Games.values();
        int nextIndex = (GameService.games.ordinal() + 1) % games.length;
        Games newGame = games[nextIndex];
        setPreviousGame(getGame());
        setGame(newGame);
        log.info("Game rotated to {}. Overwriting autosave.", newGame);

        boardService.prepBoard();
        boardService.startBoard();
        setCurrentTurn(Turn.LIGHT);
        Save newSave = new Save(
                getGame(),
                LocalDate.now().toString(),
                getCurrentTurn(),
                service.getPieceService().getPieces(),
                service.getAchievementService().getUnlockedAchievements()
        );
        saveManager.saveGame(newSave);

        if(!(getGame() == Games.SANDBOX)) {
            Ruleset rule = service.getModelService().createRuleSet(newGame);
            service.getModelService().setRule(rule);
        }
    }
}